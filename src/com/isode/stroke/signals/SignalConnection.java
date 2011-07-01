/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.signals;


public class SignalConnection {
    public final Signal onDestroyed = new Signal();

    public void disconnect() {
        onDestroyed.emit();
    }
}
