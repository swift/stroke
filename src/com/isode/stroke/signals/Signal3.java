/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.signals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An approximation of the boost::signals system with 3 parameters
 * @param <T1> Type 1
 * @param <T2> Type 2
 * @param <T3> Type 3
 */
public class Signal3<T1, T2, T3> {
    private final Map<SignalConnection, Slot3<T1, T2,T3> > binds_ = Collections.synchronizedMap(
            new HashMap<SignalConnection, Slot3<T1, T2,T3> >());

    /**
     * Add a slot which will be notified
     * @param bind slot, not null
     * @return signal connection
     */
    public SignalConnection connect(Slot3<T1, T2,T3> bind) {
        final SignalConnection connection = new SignalConnection();
        binds_.put(connection, bind);
        connection.onDestroyed.connect(new Slot() {
            public void call() {
                binds_.remove(connection);
            }
        });
        return connection;
    }

    /**
     * Notify all slots(listeners)
     * @param p1 parameter value 1
     * @param p2 parameter value 2
     * @param p3 parameter value 3
     */
    public void emit(T1 p1, T2 p2,T3 p3) {
        List<Slot3<T1,T2, T3>> binds = new ArrayList<Slot3<T1, T2, T3>>();
        binds.addAll(binds_.values());
        for (Slot3<T1, T2, T3> bind : binds) {
            bind.call(p1, p2, p3);
        }
    }

    /**
     * Remove all slots(listeners) 
     */
    public void disconnectAll() {
        binds_.clear();
    }
}
