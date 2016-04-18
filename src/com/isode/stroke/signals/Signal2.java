/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.signals;


/**
 * An approximation of the boost::signals system, although a little more warty.
 */
public class Signal2<T1, T2> extends BaseSignal {
    public SignalConnection connect(Slot2<T1, T2> bind) {
        return addBind(bind);
    }

    @SuppressWarnings("unchecked")
    public void emit(T1 p1, T2 p2) {
        final BaseSlot[] binds = getBinds();
        if (binds == null) {return;}
        for (BaseSlot bind : binds) {
            ((Slot2<T1, T2>)bind).call(p1, p2);
        }
    }
}
