/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.signals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * An approximation of the boost::signals system, although a little more warty.
 */
public class Signal1<T1> {
    private final Map<SignalConnection, Slot1<T1> > binds_ = Collections.synchronizedMap(new HashMap<SignalConnection, Slot1<T1> >());
    public SignalConnection connect(Slot1<T1> bind) {
        final SignalConnection connection = new SignalConnection();
        binds_.put(connection, bind);
        connection.onDestroyed.connect(new Slot() {
           public void call() {
               binds_.remove(connection);
           }
        });
        return connection;
    }

    public void emit(T1 p1) {
        ArrayList<Slot1<T1>> binds = new ArrayList<Slot1<T1>>();
        binds.addAll(binds_.values());
        for (Slot1<T1> bind : binds) {
            bind.call(p1);
        }
    }

    public SignalConnection connect(final Signal1<T1> target) {
        return connect(new Slot1<T1>() {
            public void call(T1 p1) {
                target.emit(p1);
            }
        });
    }

    public void disconnectAll() {
        binds_.clear();
    }
}
