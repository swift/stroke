/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.network;

import com.isode.stroke.eventloop.EventLoop;

public class JavaTLSConnectionFactory implements ConnectionFactory {

    public JavaTLSConnectionFactory(EventLoop eventLoop) {
        this.eventLoop = eventLoop;
    }

    public Connection createConnection() {
        return JavaConnection.create(eventLoop);
    }

    private final EventLoop eventLoop;
}
