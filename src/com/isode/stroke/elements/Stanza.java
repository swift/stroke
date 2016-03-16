/*
 * Copyright (c) 2010-2016, Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.elements;

import com.isode.stroke.jid.JID;

import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

/**
 * Base class for all types of XMPP stanza.
 */
public abstract class Stanza implements Element {
    private String id_ = "";
    private JID from_ = new JID();
    private JID to_ = new JID();
    private Vector<Payload> payloads_ = new Vector<Payload>();

    /**
     * Create a stanza object 
     */
    public Stanza() {        
    }
    
    /**
     * Create a stanza object copy from another
     * @param other object to be copied, not null
     */
    public Stanza(Stanza other) {
        this.id_ = other.id_;
        if(other.from_ != null) {
            this.from_ = JID.fromString(other.from_.toString());
        }
        if(other.to_!= null) {
            this.to_ = JID.fromString(other.to_.toString());
        }
        payloads_ = new Vector<Payload>(other.payloads_);
    }
    
    /**
     * Indicates if a given Payload is of a given type
     * @param <T> A type of payload
     * @param <P> A Payload
     * @param type An instance of the type of payload to check for
     * @param payload the payload to check the type of
     * @return {@code true} if the Payload is of the same type,
     * {@code false} otherwise.
     */
    private static <T extends Payload,P extends Payload> boolean isPayloadOfType(T type,P payload) {
        return payload.getClass().isAssignableFrom(type.getClass());
    }
    
    /**
     * Removes all the payloads of the given type from the stanza
     * @param <T> The payload type
     * @param type Object of the payload type to remove, should
     * not be {@code null}
     */
    public <T extends Payload> void removePayload(T type) {
        Iterator<Payload> payloadIterator = payloads_.iterator();
        while (payloadIterator.hasNext()) {
            Payload payload = payloadIterator.next();
            if (isPayloadOfType(type, payload)) {
                payloadIterator.remove();
            }
        }
    }

    /**
     * Get the payload of the given type from the stanza
     * @param <T> payload type
     * @param type payload type object instance, not null
     * @return payload of given type, can be null
     */
    @SuppressWarnings("unchecked")
	public <T extends Payload> T getPayload(T type) {
        for (Payload payload : payloads_) {
            if (isPayloadOfType(type, payload)) {
                return (T)payload;
            }
        }
        return null;
    }

    /**
     * Get the payloads of the given type from the stanza
     * @param <T> payload type
     * @param type payload type object instance, not null
     * @return list of payloads of given type, not null but can be empty
     */
    @SuppressWarnings("unchecked")
	public <T extends Payload> Vector<T> getPayloads(T type) {
        Vector<T> results = new Vector<T>();
        for (Payload payload : payloads_) {
            if (payload.getClass().isAssignableFrom(type.getClass())) {
                results.add((T)payload);
            }
        }
        return results;
    }

    /**
     * Get the list of payloads from this stanza
     * @return list of payloads, not null but can be empty
     */
    public Vector<Payload> getPayloads() {
        return payloads_;
    }

    /**
     * Add a payload to the stanza
     * @param payload payload to be added, not null
     */
    public void addPayload(Payload payload) {
        payloads_.add(payload);
    }

    /**
     * Update payload to the staza object. It will replace the payload of
     * given type  if it exists or add it the list if it does not exist
     * @param payload payload to be updated, not null
     */
    public void updatePayload(Payload payload) {
        for (int i = 0; i < payloads_.size(); i++) {
            if (payloads_.get(i).getClass() == payload.getClass()) {
                payloads_.set(i, payload);
                return;
            }
        }
        payloads_.add(payload);
    }

    /**
     * Get the jabber ID of the sender
     * @return jabber id, can be null
     */
    public JID getFrom() {
        return from_;
    }

    /**
     * Set the jabber ID of the sender
     * @param from jabber id, can be null
     */
    public void setFrom(JID from) {
        from_ = from;
    }

    /**
     * Get the jabber ID of the recipient user of the stanza
     * @return jabber id, can be null
     */
    public JID getTo() {
        return to_;
    }

    /**
     * Set the jabber ID of the recipient user of the stanza
     * @param to jabber id, can be null
     */
    public void setTo(JID to) {
        to_ = to;
    }

    /**
     * Get the identification string of the stanza
     * @return ID string, can be null if its not an IQ stanza
     */
    public String getID() {
        return id_;
    }

    /**
     * Set the identification string of the stanza
     * @param id ID string, not null for IQ stanza but can be null for
     *          Message or Presence stanza
     */
    public void setID(String id) {
        id_ = id;
    }
    
    /**
     * Returns debug-friendly String description of this Stanza, which will
     * include the subclass's name (e.g. "Presence").
     * @return a debug-friendly String.  
     */
    @Override
    public String toString() {
        String className = this.getClass().getSimpleName();

        // Include actual stanza type based on class name of the object
        return className + 
        " stanza from \"" + from_ + "\" to \"" + to_ + "\"" +
        " id=\"" + id_ + "\"";
    }

    public Date getTimestamp() {
    	Delay delay = getPayload(new Delay());
    	return delay != null ? delay.getStamp() : null;
    }
    
    public Date getTimestampFrom(final JID jid) {
        Vector<Delay> delays = getPayloads(new Delay());
        for (int i = 0; i < delays.size(); ++i) {
            Delay delay = delays.get(i);
            final JID from = delay.getFrom();
            if (from != null && from.equals(jid)) {
                return delay.getStamp();
            }
        }
        return getTimestamp();
    }

}
