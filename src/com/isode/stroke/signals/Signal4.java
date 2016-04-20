/*
 * Copyright (c) 2012-2015, Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.signals;



/**
 * An approximation of the boost::signals system with 4 parameters
 * @param <T1> Type 1
 * @param <T2> Type 2
 * @param <T3> Type 3
 * @param <T4> Type 4
 */
public class Signal4<T1, T2, T3, T4> extends BaseSignal {
    /**
     * Add a slot which will be notified
     * @param bind slot, not null
     * @return signal connection
     */
    public SignalConnection connect(Slot4<T1, T2, T3, T4> bind) {
        return addBind(bind);
    }

    /**
     * Notify all slots(listeners)
     * @param p1 parameter value 1
     * @param p2 parameter value 2
     * @param p3 parameter value 3
     * @param p4 parameter value 4
     */
    @SuppressWarnings("unchecked")
    public void emit(T1 p1, T2 p2, T3 p3, T4 p4) {
        final BaseSlot[] binds = getBinds();
        if (binds == null) {return;}
        for (BaseSlot bind : binds) {
            ((Slot4<T1, T2, T3, T4>)bind).call(p1, p2, p3, p4);
        }
    }
}
