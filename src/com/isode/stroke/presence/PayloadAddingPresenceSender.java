/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.presence;

import com.isode.stroke.elements.Payload;
import com.isode.stroke.elements.Presence;

public class PayloadAddingPresenceSender implements PresenceSender {
    private Presence lastSentPresence;
    private final PresenceSender sender;
    private Payload payload;

    public PayloadAddingPresenceSender(PresenceSender sender) {
        this.sender = sender;
    }
    
    public void sendPresence(Presence presence) {
        if (presence.isAvailable()) {
            if (presence.getTo() != null && !presence.getTo().isValid()) {
                lastSentPresence = presence;
            }
        } else {
            lastSentPresence = null;
        }
        if (payload != null) {
            Presence sentPresence = presence;
            sentPresence.updatePayload(payload);
            sender.sendPresence(sentPresence);
        } else {
            sender.sendPresence(presence);
        }
    }

    public boolean isAvailable() {
        return sender.isAvailable();
    }

    /**
     * Sets the payload to be added to outgoing presences. If initial presence
     * has been sent, this will resend the last sent presence with an updated
     * payload. Initial presence is reset when unavailable presence is sent, or
     * when reset() is called.
     */
    public void setPayload(Payload payload) {
        this.payload = payload;
        if (lastSentPresence != null) {
            sendPresence(lastSentPresence);
        }
    }

    /**
     * Resets the presence sender. This puts the presence sender back in the
     * initial state (before initial presence has been sent). This also resets
     * the chained sender.
     */
    public void reset() {
        lastSentPresence = null;
    }

}
