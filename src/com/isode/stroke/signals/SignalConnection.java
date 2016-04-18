/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.signals;

public class SignalConnection {
    interface DisconnectListener {
        void onSignalConnectionDisconnect(SignalConnection connection);
    }
    
    private final DisconnectListener listener;
    
    SignalConnection(DisconnectListener listener) {
        this.listener = listener;
    }
    
    public void disconnect() {
        listener.onSignalConnectionDisconnect(this);
    }
}
