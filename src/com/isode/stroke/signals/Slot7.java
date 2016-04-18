/*
 * Copyright (c) 2012-2015, Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.signals;

/**
 * Bind class for connecting to a signal with 7 parameters.
 * @param <T1> Type 1
 * @param <T2> Type 2
 * @param <T3> Type 3
 * @param <T4> Type 4
 * @param <T5> Type 5
 * @param <T6> Type 6
 * @param <T7> Type 7
 */
public interface Slot7<T1, T2, T3, T4, T5, T6, T7> extends BaseSlot {
    /**
     * This method will be called on notification from a signal
     * @param p1 parameter value 1
     * @param p2 parameter value 2
     * @param p3 parameter value 3
     * @param p4 parameter value 4
     * @param p5 parameter value 5
     * @param p6 parameter value 6
     * @param p7 parameter value 7
     */
    void call(T1 p1, T2 p2, T3 p3, T4 p4, T5 p5, T6 p6, T7 p7);
}
