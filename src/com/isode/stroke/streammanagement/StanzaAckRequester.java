/*
 * Copyright (c) 2011 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.streammanagement;

import com.isode.stroke.elements.Message;
import com.isode.stroke.elements.Stanza;
import com.isode.stroke.signals.Signal;
import com.isode.stroke.signals.Signal1;
import java.util.ArrayList;
import java.util.List;

public class StanzaAckRequester {

    static final long MAX_HANDLED_STANZA_COUNT = Long.parseLong("4294967295"); //boost::numeric_cast<unsigned int>((1ULL<<32) - 1);

    public StanzaAckRequester() {
        this.lastHandledStanzasCount = 0L;
    }

    public void handleStanzaSent(Stanza stanza) {
        unackedStanzas.add(stanza);
        if (stanza instanceof Message) {
            onRequestAck.emit();
        }
    }

    public void handleAckReceived(long handledStanzasCount) {
        long i = lastHandledStanzasCount;
        while (i != handledStanzasCount) {
            if (unackedStanzas.isEmpty()) {
                System.err.println("Warning: Server acked more stanzas than we sent");
                break;
            }
            Stanza ackedStanza = unackedStanzas.get(0);
            unackedStanzas.remove(0);
            onStanzaAcked.emit(ackedStanza);
            i = (i == MAX_HANDLED_STANZA_COUNT ? 0 : i + 1);
        }
        lastHandledStanzasCount = handledStanzasCount;
    }

    public Signal onRequestAck = new Signal();

    public Signal1<Stanza> onStanzaAcked = new Signal1<Stanza>();

    long lastHandledStanzasCount;

    List<Stanza> unackedStanzas = new ArrayList<Stanza>();
}
