/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.elements;

public class Presence extends Stanza {

    private Type type_;

    public enum Type {

        Available, Error, Probe, Subscribe, Subscribed, Unavailable, Unsubscribe, Unsubscribed
    };

    public Presence() {
        type_ = Type.Available;
    }

    public Presence(String status) {
        type_ = Type.Available;
        setStatus(status);
    }

    public Type getType() {
        return type_;
    }

    public void setType(Type type) {
        type_ = type;
    }

    public StatusShow.Type getShow() {
        StatusShow show = getPayload (new StatusShow());
        if (show != null) {
            return show.getType();
        }
        return type_ == Type.Available ? StatusShow.Type.Online : StatusShow.Type.None;
    }

    public void setShow(StatusShow.Type show) {
        updatePayload(new StatusShow(show));
    }

    public String getStatus() {
        Status status = getPayload(new Status());
        if (status != null) {
            return status.getText();
        }
        return "";
    }

    public void setStatus(String status) {
        updatePayload(new Status(status));
    }

    public int getPriority() {
        Priority priority = getPayload(new Priority());
        return (priority != null ? priority.getPriority() : 0);
    }

    public void setPriority(int priority) {
        updatePayload(new Priority(priority));
    }
}
