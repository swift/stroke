/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.signals;


/**
 * An approximation of the boost::signals system, although a little more warty.
 */
public final class Signal1<T1> extends BaseSignal {
    public SignalConnection connect(Slot1<T1> bind) {
        return addBind(bind);
    }

    @SuppressWarnings("unchecked")
    public void emit(T1 p1) {
        final BaseSlot[] binds = getBinds();
        if (binds == null) {return;}
        for (BaseSlot bind : binds) {
            ((Slot1<T1>)bind).call(p1);
        }
    }

    public SignalConnection connect(final Signal1<T1> target) {
        return connect(new Slot1<T1>() {
            public void call(T1 p1) {
                target.emit(p1);
            }
        });
    }
}
