/*
 * Copyright (c) 2012-2015, Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.signals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot;


/**
 * An approximation of the boost::signals system with 4 parameters
 * @param <T1> Type 1
 * @param <T2> Type 2
 * @param <T3> Type 3
 * @param <T4> Type 4
 */
public class Signal7<T1, T2, T3, T4, T5, T6, T7> {
    private final Map<SignalConnection, Slot7<T1, T2, T3, T4, T5, T6, T7> > binds_ = Collections.synchronizedMap(
            new HashMap<SignalConnection, Slot7<T1, T2, T3, T4, T5, T6, T7> >());

    /**
     * Add a slot which will be notified
     * @param bind slot, not null
     * @return signal connection
     */
    public SignalConnection connect(Slot7<T1, T2, T3, T4, T5, T6, T7> bind) {
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
     * @param p4 parameter value 4
     */
    public void emit(T1 p1, T2 p2, T3 p3, T4 p4, T5 p5, T6 p6, T7 p7) {
        List<Slot7<T1, T2, T3, T4, T5, T6, T7>> binds = new ArrayList<Slot7<T1, T2, T3, T4, T5, T6, T7>>();
        binds.addAll(binds_.values());
        for (Slot7<T1, T2, T3, T4, T5, T6, T7> bind : binds) {
            bind.call(p1, p2, p3, p4, p5, p6, p7);
        }
    }

    /**
     * Remove all slots(listeners)
     */
    public void disconnectAll() {
        binds_.clear();
    }
}
