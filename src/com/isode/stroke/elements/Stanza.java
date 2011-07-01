/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.elements;

import com.isode.stroke.jid.JID;
import java.util.Vector;

/**
 * Basic XMPP stanza.
 */
public class Stanza implements Element {
    private String id_;
    private JID from_;
    private JID to_;
    private Vector<Payload> payloads_ = new Vector<Payload>();


    public <T extends Payload> T getPayload(T type) {
        for (Payload payload : payloads_) {
            if (payload.getClass().isAssignableFrom(type.getClass())) {
                return (T)payload;
            }
        }
        return null;
    }

    public <T extends Payload> Vector<T> getPayloads(T type) {
        Vector<T> results = new Vector<T>();
        for (Payload payload : payloads_) {
            if (payload.getClass().isAssignableFrom(type.getClass())) {
                results.add((T)payload);
            }
        }
        return results;
    }

    public Vector<Payload> getPayloads() {
        return payloads_;
    }

    public void addPayload(Payload payload) {
        payloads_.add(payload);
    }

    public void updatePayload(Payload payload) {
        for (int i = 0; i < payloads_.size(); i++) {
            if (payloads_.get(i).getClass() == payload.getClass()) {
                payloads_.set(i, payload);
                return;
            }
        }
        payloads_.add(payload);
    }

    public JID getFrom() {
        return from_;
    }

    public void setFrom(JID from) {
        from_ = from;
    }

    public JID getTo() {
        return to_;
    }

    public void setTo(JID to) {
        to_ = to;
    }

    public String getID() {
        return id_;
    }

    public void setID(String id) {
        id_ = id;
    }

}
