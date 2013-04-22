/*
 * Copyright (c) 2012 - 2013, Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.signals;

/**
 * Bind class for connecting to a signal with 4 parameters.
 * @param <T1> Type 1
 * @param <T2> Type 2
 * @param <T3> Type 3
 * @param <T4> Type 4
 */
public interface Slot4<T1, T2, T3, T4> {
    /**
     * This method will be called on notification from a signal
     * @param p1 parameter value 1
     * @param p2 parameter value 2
     * @param p3 parameter value 3
     * @param p4 parameter value 4
     */
    void call(T1 p1, T2 p2, T3 p3, T4 p4);
}
