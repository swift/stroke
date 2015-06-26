/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
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
        if (body != null) {
            return body.getText();
        }
        return "";
    }

    public void setBody(String body) {
        updatePayload(new Body(body));
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
