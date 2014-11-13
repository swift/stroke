/*
 * Copyright (c) 2011-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.elements;

import com.isode.stroke.jid.JID;

/**
 * Class representing a MUC Invitation Payload
 *
 */
public class MUCInvitationPayload extends Payload {
    private boolean continuation_;
    private JID jid_;
    private String password_;
    private String reason_;
    private String thread_;
    private boolean impromptu_;


    /**
     * Create the payload 
     */
    public MUCInvitationPayload()  {
        continuation_= false;
    }

    /**
     * Set the continuation value
     * @param b value to set
     */
    public void setIsContinuation(boolean b) {
        continuation_ = b;
    }

    /**
     * Get the continuation value
     * @return continuation value
     */
    public boolean getIsContinuation() {
        return continuation_;
    }

    /**
     * Set the impromptu value
     * @param b value to set
     */
    public void setIsImpromptu(boolean b) {
        impromptu_ = b;
    }

    /**
     * Get the impromptu value
     * @return impromptu value
     */
    public boolean getIsImpromptu() {
        return impromptu_;
    }

    /**
     * Set the jabber ID
     * @param jid jabber Id, not null
     */
    public void setJID(JID jid) {
        jid_ = jid;
    }

    /**
     * Get the jabber ID
     * @return jabber ID, can be null if not set
     */
    public JID getJID(){
        return jid_;
    }

    /**
     * Set the password
     * @param password not null
     */
    public void setPassword(String password) {
        password_ = password;
    }

    /**
     * Get the password
     * @return password, can be null if not set
     */
    public String getPassword() {
        return password_;
    }

    /**
     * Set the reason text
     * @param text reason text, not null
     */
    public void setReason(String text) {
        reason_ = text;
    }

    /**
     * Get the reason value
     * @return reason value, null if not set
     */
    public String getReason() {
        return reason_;
    }

    /**
     * Set the value of describing the thread
     * @param thread thread string, not null
     */
    public void setThread(String thread) {
        thread_ = thread;
    }

    /**
     * Get the string value for thread
     * @return thread value, null if not set
     */
    public String getThread() {
        return thread_;
    }    
}
