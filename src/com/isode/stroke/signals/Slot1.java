/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.signals;

/**
 * Bind class for connecting to a signal.
 */
public interface Slot1<T1> {
    void call(T1 p1);
}
