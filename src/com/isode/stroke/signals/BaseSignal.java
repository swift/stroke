/*
 * Copyright (c) 2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.signals;

import java.util.HashMap;
import java.util.Map;

/* package */ abstract class BaseSignal implements SignalConnection.DisconnectListener {
    
    // Optimized for the case of 0 or 1 bind
    private SignalConnection connection_;
    private BaseSlot bind_;
    private Map<SignalConnection, BaseSlot> binds_;
    
    protected final synchronized SignalConnection addBind(final BaseSlot bind) {
        final SignalConnection connection = new SignalConnection(this);
        if (binds_ != null) {
            binds_.put(connection, bind);
        } else if (connection_ != null) {
            binds_ = new HashMap<SignalConnection, BaseSlot>();
            binds_.put(connection_, bind_);
            connection_ = null;
            bind_ = null;
            binds_.put(connection, bind);
        } else {
            connection_ = connection;
            bind_ = bind;
        }
        return connection;
    }
    
    protected final synchronized BaseSlot[] getBinds() {
        if (binds_ != null) {
            return binds_.values().toArray(new BaseSlot[binds_.size()]);
        } else if (connection_ != null) {
            return new BaseSlot[]{bind_};
        } else {
            return null; // return null rather than allocate an empty array
        }
    }
    
    @Override
    public final synchronized void onSignalConnectionDisconnect(final SignalConnection connection) {
        if (binds_ != null) {
            binds_.remove(connection);
        } else if (connection_ == connection) {
            connection_ = null;
            bind_ = null;
        }
    }

    public final synchronized void disconnectAll() {
        if (binds_ != null) {
            binds_.clear();
        } else {
            connection_ = null;
            bind_ = null;
        }
    }
}
