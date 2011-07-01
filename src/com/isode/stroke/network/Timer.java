/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.network;

import com.isode.stroke.signals.Signal;

public abstract class Timer {
    public abstract void start();
    public abstract void stop();
    public final Signal onTick = new Signal();
}
