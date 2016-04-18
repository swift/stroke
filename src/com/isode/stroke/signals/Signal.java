/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.signals;


/**
 * An approximation of the boost::signals system, although a little more warty.
 */
public final class Signal extends BaseSignal {

    public SignalConnection connect(Slot bind) {
        return addBind(bind);
    }

    public SignalConnection connect(final Signal target) {
        return connect(new Slot() {
            public void call() {
                target.emit();
            }
        });
    }

    public void emit() {
        final BaseSlot[] binds = getBinds();
        if (binds == null) {return;}
        for (BaseSlot bind : binds) {
            ((Slot)bind).call();
        }
    }

}
