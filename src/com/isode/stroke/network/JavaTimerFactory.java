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

public class JavaTimerFactory implements TimerFactory {

    public JavaTimerFactory(EventLoop eventLoop) {
        eventLoop_ = eventLoop;
    }

    public Timer createTimer(int milliseconds) {
        return new JavaTimer(eventLoop_, milliseconds);
    }

    private final EventLoop eventLoop_;

}
