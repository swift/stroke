/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.isode.stroke.base.SafeByteArray;
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
        public SafeByteArray dataRead_;
        public boolean socketClosed_;

        ReadResult(boolean socketClosed) {
            dataRead_ = new SafeByteArray();
            socketClosed_ = socketClosed;
        }

        ReadResult(ByteArrayOutputStream byteArrayOutputStream, boolean socketClosed) {
            dataRead_ = new SafeByteArray(byteArrayOutputStream.toByteArray());
            socketClosed_ = socketClosed;
        }
    }

    private class Worker implements Runnable {

        private final HostAddressPort address_;
        private final List<byte[]> writeBuffer_ = Collections.synchronizedList(new ArrayList<byte[]>());
        private SelectionKey selectionKey_; // not volatile - only set/tested with selectorLock_ held
        private final Object selectorLock_ = new Object(); // use private lock object that is not visible elsewhere
        private boolean disconnected_ = false;

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
                    synchronized (selectorLock_) {
                        int ops = SelectionKey.OP_READ;
                        if (isWriteNeeded()) {
                            ops |= SelectionKey.OP_WRITE; // could have been queued before selectionKey_ established
                        }
                        selectionKey_ = socketChannel_.register(Selector.open(), ops);
                    }
                } catch (IOException ex) { // includes ClosedChannelException
                    handleConnected(true);
                    return;
                }
                handleConnected(false);
                
                final SelectionKey selectionKey = selectionKey_;
                final Selector selector = selectionKey.selector();
                while (!disconnecting_ || isWriteNeeded()) {
                    int ready = 0;
                    
                    /* This will block until something is ready on the selector,
                     * including someone calling selector.wakeup(), or until the
                     * thread is interrupted
                     */
                    try {
                        /*
                         * This should actually be thread safe, despite first appearances.
                         * Selector.select() could have examined the (empty) bit mask
                         * but the mask could be updated and Selector.wakeup() called
                         * before the action OS select starts. However, Selector.wakeup()
                         * will cause the next Selector.select() to return immediately
                         * in any case. A memory barrier is required somewhere in the loop.
                         * 
                         * The Android implementation (java.nio.SelectorImpl) appears to honour
                         * the somewhat poorly worded contract of Selector.wakeup(). There is
                         * no window between testing the ops and starting the underlying select
                         * as it uses a loopback connection (pipe) to communicate the wakeup event.
                         * The same is true of the Solaris implementations (PollSelectorImpl
                         * & DevPollSelectorImpl).
                         * 
                         * It is probably reasonable to assume that OpenJDK-based implementations
                         * are thread-safe.
                         * 
                         * GCC 4.8 libjava has a thread-unsafe window (EpollSelectorImpl).
                         * 
                         * 
                         * Another option would be to wait on selectorLock_ while selectionKey's
                         * interestOps is empty (and not disconnecting). Since bits are only ever
                         * removed from the set by this thread (even though they are added by others)
                         * then such a strategy would remove any chance of selecting with an empty mask,
                         * and the consequent issue of a potentially (buggy)thread-unsafe implementation
                         * of Selector.wakeup(). This would complicate the inter-thread communication,
                         * requiring signalling the lock as well as calling Selector.wakeup(). It is
                         * also possible that a weird read-before-write requirement when a select only
                         * for write is active would still not be interrupted, so it would not solve
                         * everything.
                         */
                        ready = selector.select();
                    } catch (ClosedSelectorException e) {
                        break;
                    } catch (IOException e) {
                        break;
                    }
                    
                    /* See what needs doing */
                    boolean writeNeeded = false;
                    boolean readNeeded = false;
                    /*
                     * This synchronized block serves two purposes:
                     *   1. Guard against the small chance that selectionKey.interestOps()
                     *      may be thread-unsafe relative to setInterestOp() below.
                     *   2. Provide a memory barrier to ensure that updated select ops are
                     *      reflected in this thread's view of the world.
                     */
                    synchronized (selectorLock_) {
                        try {
                            if (ready > 0) {
                                final int ops = selectionKey.interestOps() & selectionKey.readyOps();
                                writeNeeded = (ops & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE;
                                readNeeded  = (ops & SelectionKey.OP_READ)  == SelectionKey.OP_READ;
                                selector.selectedKeys().clear();
                            }
                        } catch (CancelledKeyException e) {
                            // leave it to select to catch the disconnect
                        }
                    }
                    
                    /* Handle any writing */
                    if (writeNeeded) {
                        try {
                            doWrite();
                            synchronized (selectorLock_) {
                                if (!isWriteNeeded()) {
                                    clearInterestOp(SelectionKey.OP_WRITE);
                                }
                            }
                        }
                        catch (IOException e) {
                            disconnecting_ = true;
                            handleDisconnected(Error.WriteError);
                        }
                    }

                    /* Handle any reading */
                    if (readNeeded) {
                        final ReadResult rr = doRead();
                        final SafeByteArray dataRead = rr.dataRead_;
                        if (!dataRead.isEmpty()) {
                            handleDataRead(dataRead);
                        }
                        if (rr.socketClosed_) {
                            handleDisconnected(Error.ReadError);
                            return;
                        }
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
                    if(selectionKey_ != null) {
                        try {
                            synchronized (selectorLock_) {
                                selectionKey_.selector().close();
                            }
                        } catch (IOException e) {
                        }
                    }
                }
            }
        }
        
        /**
         * Set one or more SelectionKey bits in the select mask.
         * 
         * May be called from outside Worker thread
         * May recursively lock selectorLock_
         * 
         * @param op - OP_READ | OP_WRITE: may be 0 just to force wakeup
         */
        private void setInterestOp(int op) {
            synchronized (selectorLock_) {
                final SelectionKey key = selectionKey_;
                if (key != null && key.isValid()) {
                    try {
                        key.interestOps(key.interestOps() | op);
                        
                        /*
                         * We could check that we have actually changed the mask before
                         * invoking a wakeup. The usage pattern, however, is such that
                         * such a check would almost always be true.
                         */
                        final Selector selector = key.selector();
                        // Check "isOpen" to avoid Android crash see
                        //   https://code.google.com/p/android/issues/detail?id=80785
                        if (selector.isOpen()) {
                            selector.wakeup();
                        }
                    } catch (CancelledKeyException e) {
                        // channel has been closed
                    }
                }
            }
        }

        /**
         * Clear one or more SelectionKey bits in the select mask.
         * 
         * May be called from outside Worker thread
         * May recursively lock selectorLock_
         * 
         * @param op - OP_READ | OP_WRITE
         */
        private void clearInterestOp(int op) {
            synchronized (selectorLock_) {
                final SelectionKey key = selectionKey_;
                if (key != null && key.isValid()) {
                    try {
                        key.interestOps(key.interestOps() & ~op);
                        // No need to wakeup the selector
                    } catch (CancelledKeyException e) {
                        // channel has been closed
                    }
                }
            }
        }

        /**
         * Called from outside Worker thread
         */
        public void queueWrite(byte[] data) {
            synchronized (selectorLock_) {
                writeBuffer_.add(data);
                if (writeBuffer_.size() == 1) {
                    setInterestOp(SelectionKey.OP_WRITE);
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

        private void handleDataRead(final SafeByteArray data) {
            if (synchroniseReads_) {
                clearInterestOp(SelectionKey.OP_READ);
            }
            eventLoop_.postEvent(new Callback() {
                public void run() {
                    onDataRead.emit(data);
                    if (synchroniseReads_) {
                        setInterestOp(SelectionKey.OP_READ);
                    }
                }
            });
        }

    }

    private JavaConnection(EventLoop eventLoop, boolean synchroniseReads) {
        eventLoop_ = eventLoop;
        synchroniseReads_ = synchroniseReads;
    }

    public static JavaConnection create(EventLoop eventLoop) {
        return new JavaConnection(eventLoop, false);
    }

    /**
     * Creates a new JavaConnection
     * @param eventLoop the EventLoop for read and write events to be posted to
     * @param synchroniseReads if true then data will not be read from the connection
     * until the previous read has been processed by the EventLoop
     * @return a new JavaConnection
     */
    public static JavaConnection create(EventLoop eventLoop, boolean synchroniseReads) {
        return new JavaConnection(eventLoop, synchroniseReads);
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
        if (worker_ != null) {
            worker_.setInterestOp(0); // force wakeup
        }
    }

    @Override
    public void write(SafeByteArray data) {
        worker_.queueWrite(data.getData());
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
    private volatile boolean disconnecting_ = false;
    private SocketChannel socketChannel_;
    private Worker worker_;
    private final boolean synchroniseReads_;

}
