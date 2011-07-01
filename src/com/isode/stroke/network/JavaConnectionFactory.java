/*
 * Copyright (c) 2010 Remko Tronçon
 * Licensed under the GNU General Public License v3.
 * See Documentation/Licenses/GPLv3.txt for more information.
 */
/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.network;

import com.isode.stroke.eventloop.EventLoop;

public class JavaConnectionFactory implements ConnectionFactory {

    public JavaConnectionFactory(EventLoop eventLoop) {
        this.eventLoop = eventLoop;
    }

    public Connection createConnection() {
        return JavaConnection.create(eventLoop);
    }

    private final EventLoop eventLoop;
}
