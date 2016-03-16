/*
 * Copyright (c) 2010-2016, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.elements;

public class Message extends Stanza {

    private Type type_ = Type.Chat;

    public enum Type {

        Normal, Chat, Error, Groupchat, Headline
    };

    public String getSubject() {
        Subject subject = getPayload(new Subject());
        if (subject != null) {
            return subject.getText();
        }
        return "";
    }
    
    public boolean hasSubject() {
        return getPayload(new Subject()) != null;
    }

    public void setSubject(String subject) {
        updatePayload(new Subject(subject));
    }

    public String getBody() {
        Body body = getPayload(new Body());
        String bodyData = null;
        if (body != null) {
            bodyData = body.getText();
        }
        return bodyData;
    }

    public void setBody(String body) {
        if (body != null) {
            updatePayload(new Body(body));
        }
        else {
            removePayload(new Body());
        }
    }

    public boolean isError() {
        ErrorPayload error = getPayload(new ErrorPayload());
        return getType().equals(Type.Error) || error != null;
    }

    public Type getType() {
        return type_;
    }

    public void setType(Type type) {
        type_ = type;
    }
    
    @Override
    public String toString() {
        return super.toString() + " subject=\"" + getSubject() + "\""; 
    }
}
