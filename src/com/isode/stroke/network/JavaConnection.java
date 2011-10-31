/*
 * Copyright (c) 2010 Remko Tronçon
 * Licensed under the GNU General Public License v3.
 * See Documentation/Licenses/GPLv3.txt for more information.
 */
/*
 * Copyright (c) 2010-2011, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.network;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.eventloop.Event.Callback;
import com.isode.stroke.eventloop.EventLoop;
import com.isode.stroke.eventloop.EventOwner;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaConnection extends Connection implements EventOwner {

    private class Worker implements Runnable {

        private final HostAddressPort address_;
        private OutputStream write_;
        private BufferedReader read_;
        private final List<ByteArray> writeBuffer_ = Collections.synchronizedList(new ArrayList<ByteArray>());

        public Worker(HostAddressPort address) {
            address_ = address;
        }

        public void run() {
            try {
                socket_ = new Socket(address_.getAddress().getInetAddress(), address_.getPort());
                write_ = socket_.getOutputStream();
                read_ = new BufferedReader(new InputStreamReader(socket_.getInputStream(), "utf-8"));
            } catch (IOException ex) {
                handleConnected(true);
                return;
            }
            handleConnected(false);
            while (!disconnecting_) {
                boolean didWrite = false;
                while (!writeBuffer_.isEmpty()) {
                    didWrite = true;
                    ByteArray data = writeBuffer_.get(0);
                    for (byte datum : data.getData()) {
                        try {
                            write_.write(datum);
                        } catch (IOException ex) {
                            disconnecting_ = true;
                            handleDisconnected(Error.WriteError);
                        }
                    }
                    writeBuffer_.remove(0);
                }
                if (didWrite && !disconnecting_) {
                    try {
                        write_.flush();
                    } catch (IOException ex) {
                        disconnecting_ = true;
                        handleDisconnected(Error.WriteError);
                    }
                }
                ByteArray data = new ByteArray();
                try {
                    while (read_.ready()) {
                        char[] c = new char[1024];
                        int i = read_.read(c, 0, c.length);
                        if (i > 0) {
                            data.append(new String(c, 0, i));
                        }
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
            try {
                read_.close();
                write_.close();
                socket_.close();
            } catch (IOException ex) {
                /* Do we need to return an error if we're already trying to close? */
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
            eventLoop_.postEvent(new Callback() {
                public void run() {
                    onDisconnected.emit(error);
                }
            });
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
        return new HostAddressPort(new HostAddress(socket_.getLocalAddress()), socket_.getLocalPort());
    }
    private final EventLoop eventLoop_;
    private boolean disconnecting_ = false;
    private Socket socket_;
    private Worker worker_;
}
