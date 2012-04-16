/*
 * Copyright (c) 2010-2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.elements;

/**
 * Class representing Presence stanza
 *
 */
public class Presence extends Stanza {

    private Type type_;

    /**
     * Presence Stanza Type
     *
     */
    public enum Type {

        Available, Error, Probe, Subscribe, Subscribed, Unavailable, Unsubscribe, Unsubscribed
    };

    /**
     * Create the Presence object 
     */
    public Presence() {
        type_ = Type.Available;
    }
    
    /**
     * Create a Presence object from the specified object by
     * creating a copy
     * @param copy presence object to be copied, not null
     */
    public Presence(Presence copy) {
        super(copy);
        this.type_ = copy.type_;
    }

    /**
     * Create the Presence object using the status string
     * @param status status string, not null
     */
    public Presence(String status) {
        type_ = Type.Available;
        setStatus(status);
    }

    /**
     * Get the Presence Stanza type
     * @return presence type, not null
     */
    public Type getType() {
        return type_;
    }

    /**
     * Set the Presence stanza type
     * @param type stanza type, not null
     */
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

    /**
     * Set the show status type
     * @param show status type, not null
     */
    public void setShow(StatusShow.Type show) {
        updatePayload(new StatusShow(show));
    }

    /**
     * Get the status message
     * @return status message, not null but can be empty
     */
    public String getStatus() {
        Status status = getPayload(new Status());
        if (status != null) {
            return status.getText();
        }
        return "";
    }

    /**
     * Set the status string
     * @param status status string, not null
     */
    public void setStatus(String status) {
        updatePayload(new Status(status));
    }

    /**
     * Get the priority of presence stanza
     * @return priority of presence stanza
     */
    public int getPriority() {
        Priority priority = getPayload(new Priority());
        return (priority != null ? priority.getPriority() : 0);
    }

    /**
     * Set the priority of presence message.
     * @param priority priority value (allowed values -127 to 128)
     */
    public void setPriority(int priority) {
        updatePayload(new Priority(priority));
    }

    @Override
    public String toString() {
        return super.toString() + " Type=" + type_; 
    }
}
