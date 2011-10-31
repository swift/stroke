/*
 * Copyright (c) 2011 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.streammanagement;

import com.isode.stroke.signals.Signal1;

public class StanzaAckResponder {

    static final long MAX_HANDLED_STANZA_COUNT = Long.parseLong("4294967295"); //boost::numeric_cast<unsigned int>((1ULL<<32) - 1);

    public StanzaAckResponder() {
    }

    public void handleStanzaReceived() {
        handledStanzasCount = (handledStanzasCount == MAX_HANDLED_STANZA_COUNT ? 0 : handledStanzasCount + 1);
    }

    public void handleAckRequestReceived() {
        onAck.emit(handledStanzasCount);
    }
    public Signal1<Long> onAck = new Signal1<Long>();
    private long handledStanzasCount = 0;
}
