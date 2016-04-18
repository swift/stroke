/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.signals;

/**
 * Bind class for connecting to a signal.
 */
public interface Slot2<T1, T2> extends BaseSlot {
    void call(T1 p1, T2 p2);
}
