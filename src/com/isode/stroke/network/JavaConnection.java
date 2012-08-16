/*
 * Copyright (c) 2010 Remko Tronçon
 * All rights reserved.
 */
/*
 * Copyright (c) 2010-2012, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.eventloop.Event.Callback;
import com.isode.stroke.eventloop.EventLoop;
import com.isode.stroke.eventloop.EventOwner;

public class JavaConnection extends Connection implements EventOwner {

    private class Worker implements Runnable {

        private final HostAddressPort address_;
        private final List<ByteArray> writeBuffer_ = Collections.synchronizedList(new ArrayList<ByteArray>());

        public Worker(HostAddressPort address) {
            address_ = address;
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
                } catch (IOException ex) {
                    handleConnected(true);
                    return;
                }
                handleConnected(false);
                while (!disconnecting_) {
                    while (!writeBuffer_.isEmpty()) {
                        ByteArray data = writeBuffer_.get(0);
                        ByteBuffer byteBuffer = ByteBuffer.wrap(data.getData());
                        try {
                            /* Because the SocketChannel is non-blocking, we have to
                             * be prepared to cope with the write operation not
                             * consuming all of the data
                             */
                            boolean finishedWriting = false;
                            while (!finishedWriting && !disconnecting_) {                     
                                socketChannel_.write(byteBuffer);
                                finishedWriting = (byteBuffer.remaining() == 0);
                                if (!finishedWriting) {
                                    try {
                                        /* Give the output buffer a chance to empty */
                                        Thread.sleep(100);
                                    }
                                    catch (InterruptedException e) {
                                        /* Perhaps someone has set disconnecting_ */
                                    }
                                }
                            }
                        } catch (IOException ex) {
                            disconnecting_ = true;
                            handleDisconnected(Error.WriteError);                    
                        }
                        writeBuffer_.remove(0);
                    }

                    ByteArray data = new ByteArray();
                    try {
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

                        int count = socketChannel_.read(byteBuffer);
                        while (count > 0) {
                            byteBuffer.flip();
                            byte[] result = new byte[byteBuffer.remaining()];
                            byteBuffer.get(result);
                            byteBuffer.compact();
                            for (int i=0; i<result.length; i++) {
                                data.append(result[i]);
                            }
                            count = socketChannel_.read(byteBuffer);
                        }
                        if (count == -1) {
                            /* socketChannel input has reached "end-of-stream", which
                             * we regard as meaning that the socket has been closed 
                             */
                            throw new IOException("socketChannel_.read returned -1");
                        }
                    } catch (IOException ex) {
                        handleDisconnected(Error.ReadError);
                        return;
                    }

                    if (!data.isEmpty()) {
                        handleDataRead(data);
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        /* We've been woken up, probably to force us to do something.*/
                    }
                }            
                handleDisconnected(null);
            }finally {
                if(socketChannel_ != null) {
                    try {
                        socketChannel_.close();
                    } catch (IOException ex) {
                        /* Do we need to return an error if we're already trying to close? */
                    }
                }
            }
        }

        private void handleConnected(final boolean error) {
            eventLoop_.postEvent(new Callback() {
                public void run() {
                    onConnectFinished.emit(error);
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

        public void write(ByteArray data) {
            writeBuffer_.add(data);
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
        workerThread.start();
    }

    @Override
    public void disconnect() {
        disconnecting_ = true;
    }

    @Override
    public void write(ByteArray data) {
        worker_.writeBuffer_.add(data);
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
    private Worker worker_;

}
