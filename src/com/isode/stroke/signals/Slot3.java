/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.signals;

/**
 * Bind class for connecting to a signal with 3 parameters.
 * @param <T1> Type 1
 * @param <T2> Type 2
 * @param <T3> Type 3
 */
public interface Slot3<T1, T2,T3> extends BaseSlot {
    /**
     * This method will be called on notification from a signal
     * @param p1 parameter value 1
     * @param p2 parameter value 2
     * @param p3 parameter value 3
     */
    void call(T1 p1, T2 p2,T3 p3);
}
