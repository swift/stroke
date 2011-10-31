/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.elements;

/**
 * Error.
 */
public class ErrorPayload extends Payload {
    private Condition condition_;
    private Type type_;
    private String text_;
    
    public enum Type { Cancel, Continue, Modify, Auth, Wait };

    public enum Condition {
        BadRequest,
        Conflict,
        FeatureNotImplemented,
        Forbidden,
        Gone,
        InternalServerError,
        ItemNotFound,
        JIDMalformed,
        NotAcceptable,
        NotAllowed,
        NotAuthorized,
        PaymentRequired,
        RecipientUnavailable,
        Redirect,
        RegistrationRequired,
        RemoteServerNotFound,
        RemoteServerTimeout,
        ResourceConstraint,
        ServiceUnavailable,
        SubscriptionRequired,
        UndefinedCondition,
        UnexpectedRequest
    };

    public ErrorPayload(Condition condition, Type type, String text) {
        condition_ = condition;
        type_ = type;
        text_ = text;
    }

    public ErrorPayload(Condition condition, Type type) {
        this(condition, type, "");
    }

    public ErrorPayload(Condition condition) {
        this(condition, Type.Cancel);
    }

    public ErrorPayload() {
        this(Condition.UndefinedCondition);
    }

    public Type getType() {
        return type_;
    }

    public void setType(Type type) {
        type_ = type;
    }

    public Condition getCondition() {
        return condition_;
    }

    public void setCondition(Condition condition) {
        condition_ = condition;
    }

    public void setText(String text) {
        text_ = text;
    }

    public String getText() {
        return text_;
    }
}
