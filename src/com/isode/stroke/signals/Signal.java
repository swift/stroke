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
public class Signal {

    private final Map<SignalConnection, Slot> binds_ = Collections.synchronizedMap(new HashMap<SignalConnection, Slot>());

    public SignalConnection connect(Slot bind) {
        final SignalConnection connection = new SignalConnection();
        binds_.put(connection, bind);
        connection.onDestroyed.connectWithoutReturn(new Slot() {

            public void call() {
                binds_.remove(connection);
            }
        });
        return connection;
    }

    public SignalConnection connect(final Signal target) {
        return connect(new Slot() {
            public void call() {
                target.emit();
            }
        });
    }

    void connectWithoutReturn(Slot bind) {
        binds_.put(null, bind);
    }

    public void emit() {
        ArrayList<Slot> binds = new ArrayList<Slot>();
        binds.addAll(binds_.values());
        for (Slot bind : binds) {
            bind.call();
        }
    }

    public void disconnectAll() {
        binds_.clear();
    }
}
