/*
 * Copyright (c) 2011, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.elements;

public class StreamError implements Element {

    public enum Type {

        BadFormat,
        BadNamespacePrefix,
        Conflict,
        ConnectionTimeout,
        HostGone,
        HostUnknown,
        ImproperAddressing,
        InternalServerError,
        InvalidFrom,
        InvalidID,
        InvalidNamespace,
        InvalidXML,
        NotAuthorized,
        NotWellFormed,
        PolicyViolation,
        RemoteConnectionFailed,
        Reset,
        ResourceConstraint,
        RestrictedXML,
        SeeOtherHost,
        SystemShutdown,
        UndefinedCondition,
        UnsupportedEncoding,
        UnsupportedStanzaType,
        UnsupportedVersion,
    };

    public StreamError() {
        this(Type.UndefinedCondition);
    }

    public StreamError(Type type) {
        this(type, "");
    }

    public StreamError(Type type, String text) {
        if (type == null) {
            throw new IllegalStateException();
        }
        type_ = type;
        text_ = text;
    }

    public Type getType() {
        return type_;
    }

    public void setType(Type type) {
        type_ = type;
    }

    public void setText(String text) {
        text_ = text;
    }

    public String getText() {
        return text_;
    }
    private Type type_;
    private String text_;
};
