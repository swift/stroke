/*
 * Copyright (c) 2010 Remko Tronçon
 * All rights reserved.
 */
/*
 * Copyright (c) 2010-2013, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.eventloop.Event.Callback;
import com.isode.stroke.eventloop.EventLoop;
import com.isode.stroke.eventloop.EventOwner;

public class JavaConnection extends Connection implements EventOwner {
    
    /**
     * Wrapper class that is used by the "doRead" method so that it can
     * return both a ByteArray and an indication of whether the socket
     * got closed.
     */
    private static class ReadResult {
        public ByteArray dataRead_;
        public boolean socketClosed_;

        ReadResult(boolean socketClosed) {
            dataRead_ = new ByteArray();
            socketClosed_ = socketClosed;
        }

        ReadResult(ByteArrayOutputStream byteArrayOutputStream, boolean socketClosed) {
            dataRead_ = new ByteArray(byteArrayOutputStream.toByteArray());
            socketClosed_ = socketClosed;
        }
    }

    private class Worker implements Runnable {

        private final HostAddressPort address_;
        private final List<byte[]> writeBuffer_ = Collections.synchronizedList(new ArrayList<byte[]>());

        public Worker(HostAddressPort address) {
            address_ = address;
        }
        
        private boolean isWriteNeeded() {
            return (!writeBuffer_.isEmpty());
        }

        public void run() {
            try {
                try {
                    socketChannel_ = SocketChannel.open(
                            new InetSocketAddress(address_.getAddress().getInetAddress(),address_.getPort()));                
                    /* By default, SocketChannels start off in blocking mode, which
                     * isn't what we want
                     */
                    socketChannel_.configureBlocking(false);
                    selector_ = Selector.open();
                    selectionKey_ = socketChannel_.register(selector_,  SelectionKey.OP_READ);
                } catch (IOException ex) {
                    handleConnected(true);
                    return;
                }
                handleConnected(false);
                while (!disconnecting_) {

                    /* This will block until something is ready on the selector,
                     * including someone calling selector.wakeup(), or until the
                     * thread is interrupted
                     */
                    try {
                        selector_.select();
                    } catch (IOException e) {
                        disconnected_ = true;
                        handleDisconnected(null);
                        break;
                    }

                    /* Something(s) happened.  See what needs doing */
                    if (disconnecting_) {
                        handleDisconnected(null);
                        /* No point doing anything else */
                        break;
                    }
                    boolean writeNeeded = isWriteNeeded();
                    boolean readNeeded = selectionKey_.isReadable();
                    
                    { /* Handle any writing */
                        if (writeNeeded) {
                            try {
                                doWrite();
                            }
                            catch (IOException e) {
                                disconnecting_ = true;
                                handleDisconnected(Error.WriteError);                                                
                            }
                        }
                    }

                    { /* Handle any reading */
                        ByteArray dataRead;

                        if (readNeeded) {
                            ReadResult rr = doRead();
                            dataRead = rr.dataRead_;
                            if (!dataRead.isEmpty()) {
                                handleDataRead(dataRead);
                            }
                            if (rr.socketClosed_) {
                                handleDisconnected(Error.ReadError);
                                return;
                            }
                        }
                    }
                    
                    if (isWriteNeeded() && !disconnected_) {
                        /* There is something that's not been written yet.
                         * This might happen because the "doWrite()" didn't
                         * write the complete buffer, or because our "write()" 
                         * method was called (perhaps more than once) since 
                         * this thread was woken.
                         * 
                         * Give the buffer a chance to empty
                         */
                        try {
                            Thread.sleep(100);
                        }
                        catch (InterruptedException e) {
                            /* */
                        }
                        /* Force the next iteration of the loop to wake up
                         * straight away, and check all conditions again
                         */
                        selector_.wakeup();
                    }
                }            
                handleDisconnected(null);
            } finally {
                if(socketChannel_ != null) {
                    try {
                        socketChannel_.close();                        
                    } catch (IOException ex) {
                        /* Do we need to return an error if we're already trying to close? */
                    }
                    if(selector_ != null) {
                        try {
                            selector_.close();
                        } catch (IOException e) {
                        }
                    }
                }
            }
        }

        /**
         * Called when there's something in the writeBuffer to be written.
         * Will remove from writeBuffer_ anything that got written.
         * @throws IOException if an error occurs when trying to write to the
         * socket
         */
        private void doWrite() throws IOException {
            if (!isWriteNeeded()) {
                return;
            }

            byte[] bytes = writeBuffer_.get(0);
            int bytesToWrite = bytes.length;

            if (bytesToWrite == 0) {
                /*
                 * Not sure if this can happen, but does no harm to check
                 */
                writeBuffer_.remove(0);
                return;
            }
            
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            /*
             * Because the SocketChannel is non-blocking, we have to
             * be prepared to cope with the write operation not
             * consuming all (or any!) of the data
             */
            boolean finishedWriting = false;
            int bytesWritten = socketChannel_.write(byteBuffer);
            final boolean somethingWasWritten = (bytesWritten != 0);
            if (somethingWasWritten) {
                eventLoop_.postEvent(new Callback() {
                    public void run() {
                        onDataWritten.emit();
                    }
                });
            }
            finishedWriting = (byteBuffer.remaining() == 0);
            if (finishedWriting) {
                writeBuffer_.remove(0);
                return;
            }
            /* Was anything written at all? */
            if (!somethingWasWritten) {
                /* Leave the buffer in the array so that it'll get tried
                 * again later
                 */
                return;
            }

            /* The buffer was *partly* written.  This means we have to
             * remove that part.  We do this by creating a new byte[]
             * with the remaining bytes in, and replacing the first 
             * element in the list with that.
             */
            byte[] remainingBytes = new byte[bytesToWrite - bytesWritten];
            remainingBytes = Arrays.copyOfRange(bytes, bytesWritten, bytes.length);
            writeBuffer_.set(0, remainingBytes);
            return;
        }
        
        /**
         * Called when there's something that's come in on the socket. The ReadResult
         * returned will contain any data that was read from the socket and a
         * flag to say whether the socket has been closed.
         * <p>If the socket has been closed, it may still be the case that data
         * was read before the close happened. 
         * @return a ReadResult containing bytes read (may be empty, won't be null),
         * and an indication of whether the was closed.
         */
        private ReadResult doRead() {

            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            int count;
            try {
                count = socketChannel_.read(byteBuffer);
            }
            catch (IOException e) {
                // Nothing read and the socket's closed
                return new ReadResult(true);
            }
            if (count == 0) {
                // Nothing read, but socket's open
                return new ReadResult(false);
            }
            boolean isClosed = false;
            ByteArrayOutputStream byteArrayOutputStream = 
                    new ByteArrayOutputStream(1024);
            
            while (count > 0) {
                byteBuffer.flip();
                byte[] result = new byte[byteBuffer.remaining()];
                byteBuffer.get(result);
                byteBuffer.compact();
                try {
                    byteArrayOutputStream.write(result);
                    count = socketChannel_.read(byteBuffer);
                }
                catch (IOException e) {
                    // Force exit from loop and indicate socket closed
                    count = -1;
                }
            }
            if (count == -1) {
                /* socketChannel input has reached "end-of-stream", which
                 * we regard as meaning that the socket has been closed 
                 */
                isClosed = true;
            }
            
            /* There is no need to close the ByteArrayOutputStream */
            return new ReadResult(byteArrayOutputStream, isClosed);

        }
        
        private void handleConnected(final boolean error) {

            eventLoop_.postEvent(new Callback() {
                public void run() {
                    onConnectFinished.emit(Boolean.valueOf(error));
                }
            });
        }

        private void handleDisconnected(final Error error) {
            if (!disconnected_) {
                disconnected_ = true;
                eventLoop_.postEvent(new Callback() {
                    public void run() {
                        onDisconnected.emit(error);
                    }
                });
            }
        }

        private void handleDataRead(final ByteArray data) {
            eventLoop_.postEvent(new Callback() {
                public void run() {
                    onDataRead.emit(data);
                }
            });
        }

    }

    private JavaConnection(EventLoop eventLoop) {
        eventLoop_ = eventLoop;
    }

    public static JavaConnection create(EventLoop eventLoop) {
        return new JavaConnection(eventLoop);
    }

    @Override
    public void listen() {
        //TODO: needed for server, not for client.
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void connect(HostAddressPort address) {
        worker_ = new Worker(address);
        Thread workerThread = new Thread(worker_);
        workerThread.setDaemon(true);
        workerThread.setName("JavaConnection "+ address.toString());
        workerThread.start();
    }

    @Override
    public void disconnect() {
        disconnecting_ = true;
        if (selector_ != null) {
            selector_.wakeup();
        }
    }

    @Override
    public void write(ByteArray data) {
        worker_.writeBuffer_.add(data.getData());
        if (selector_ != null) {
            selector_.wakeup();
        }

    }

    @Override
    public HostAddressPort getLocalAddress() {
        if (socketChannel_ == null) {
            return null;
        }
        Socket socket = socketChannel_.socket();
        if (socket == null) {
            return null;
        }
        return new HostAddressPort(new HostAddress(socket.getLocalAddress()), socket.getLocalPort());        
    }
    
    @Override
    public String toString()
    {
        return "JavaConnection " + 
        (socketChannel_ == null ? "with no socket configured" : "for " + getLocalAddress()) +
        (disconnecting_ ? " (disconnecting)" : "");
    }
    
    private final EventLoop eventLoop_;
    private boolean disconnecting_ = false;
    private boolean disconnected_ = false;
    private SocketChannel socketChannel_;
    private Selector selector_;
    private SelectionKey selectionKey_;
    private Worker worker_;

}
