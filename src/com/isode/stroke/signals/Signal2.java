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
public class Signal2<T1, T2> {
    private final Map<SignalConnection, Slot2<T1, T2> > binds_ = Collections.synchronizedMap(new HashMap<SignalConnection, Slot2<T1, T2> >());
    public SignalConnection connect(Slot2<T1, T2> bind) {
        final SignalConnection connection = new SignalConnection();
        binds_.put(connection, bind);
        connection.onDestroyed.connect(new Slot() {
           public void call() {
               binds_.remove(connection);
           }
        });
        return connection;
    }

    public void emit(T1 p1, T2 p2) {
        ArrayList<Slot2<T1,T2>> binds = new ArrayList<Slot2<T1, T2>>();
        binds.addAll(binds_.values());
        for (Slot2<T1, T2> bind : binds) {
            bind.call(p1, p2);
        }
    }

    public void disconnectAll() {
        binds_.clear();
    }
}
