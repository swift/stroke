/*
 * Copyright (c) 2010-2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.elements;

import com.isode.stroke.jid.JID;

public class IQ extends Stanza {
    public enum Type {Get, Set, Result, Error}

    private Type type_;

    public IQ() {
        this(Type.Get);
    }

    public IQ(Type type) {
        type_ = type;
    }

    public Type getType() {
        return type_;
    }

    public void setType(Type type) {
        type_ = type;
    }

    public static IQ createRequest(Type type, JID to, String id, Payload payload) {
        IQ iq = new IQ(type);
        if(to.isValid()) {
            iq.setTo(to);            
        }
        iq.setID(id);
        if(payload != null) {
            iq.addPayload(payload);
        }
        return iq;
    }

    public static IQ createResult(JID to, String id) {
        return createResult(to, id, null);
    }

    public static IQ createResult(JID to, String id, Payload payload) {
        IQ iq = new IQ(Type.Result);
        iq.setTo(to);
        iq.setID(id);
        if(payload != null) {
            iq.addPayload(payload);
        }
        return iq;
    }

    public static IQ createResult(JID to, JID from, String id) {
        return createResult(to, from, id, null);
    }

    public static IQ createResult(JID to, JID from, String id, Payload payload) {
        IQ iq = new IQ(Type.Result);
        iq.setTo(to);
        iq.setFrom(from);
        iq.setID(id);
        if(payload != null) {
            iq.addPayload(payload);
        }
        return iq;
    }

    public static IQ createError(JID to, String id) {
        return createError(to, id, ErrorPayload.Condition.BadRequest, ErrorPayload.Type.Cancel, null);
    }

    public static IQ createError(JID to, String id, ErrorPayload.Condition condition) {
        return createError(to, id, condition, ErrorPayload.Type.Cancel, null);
    }

    public static IQ createError(JID to, String id, ErrorPayload.Condition condition, ErrorPayload.Type type) {
        return createError(to, id, condition, type, null);
    }
    
    public static IQ createError(JID to, String id, ErrorPayload.Condition condition, ErrorPayload.Type type, Payload payload) {
        IQ iq = new IQ(Type.Error);
        iq.setTo(to);
        iq.setID(id);
        ErrorPayload errorPayload = new ErrorPayload(condition, type);
        errorPayload.setPayload(payload);
        iq.addPayload(errorPayload);
        return iq;
    }

    public static IQ createError(JID to, JID from, String id) {
        return createError(to, from, id, ErrorPayload.Condition.BadRequest, ErrorPayload.Type.Cancel, null);
    }

    public static IQ createError(JID to, JID from, String id, ErrorPayload.Condition condition) {
        return createError(to, from, id, condition, ErrorPayload.Type.Cancel, null);
    }

    public static IQ createError(JID to, JID from, String id, ErrorPayload.Condition condition, ErrorPayload.Type type) {
        return createError(to, from, id, condition, type, null);
    }

    public static IQ createError(JID to, JID from, String id, ErrorPayload.Condition condition, ErrorPayload.Type type, Payload payload) {
        IQ iq = new IQ(Type.Error);
        iq.setTo(to);
        iq.setFrom(from);
        iq.setID(id);
        ErrorPayload errorPayload = new ErrorPayload(condition, type);
        errorPayload.setPayload(payload);
        iq.addPayload(errorPayload);
        return iq;
    }
    
    @Override
    public String toString() {
        return super.toString() + " Type=" + type_; 
    }
}
