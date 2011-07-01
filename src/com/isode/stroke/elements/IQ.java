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

public class IQ extends Stanza {
    public enum Type {Get, Set, Result, Error};

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
        iq.setTo(to);
        iq.setID(id);
        iq.addPayload(payload);
        return iq;
    }

    public static IQ createResult(JID to, String id, Payload payload) {
        IQ iq = new IQ(Type.Result);
        iq.setTo(to);
        iq.setID(id);
        iq.addPayload(payload);
        return iq;
    }

    public static IQ createError(JID to, String id, ErrorPayload.Condition condition, ErrorPayload.Type type) {
        IQ iq = new IQ(Type.Error);
        iq.setTo(to);
        iq.setID(id);
        iq.addPayload(new ErrorPayload(condition, type));
        return iq;
    }

}
