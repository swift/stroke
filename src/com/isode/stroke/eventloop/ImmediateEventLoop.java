/*
 * Copyright (c) 2010-14, Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.eventloop;

/**
 * Don't use this, it simply runs the callback in the same thread.
 * It is useful for unit testing, but will break GUIs.
 */
public class ImmediateEventLoop extends EventLoop {
    @Override
    protected void post(Event event) {
        handleEvent(event);
    }
}
