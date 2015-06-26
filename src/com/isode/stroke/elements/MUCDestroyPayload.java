/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2011, Kevin Smith
 * All rights reserved.
 */
package com.isode.stroke.elements;

import com.isode.stroke.jid.JID;

/**
 * Class representing MUC Destroy Payload
 *
 */
public class MUCDestroyPayload extends Payload{
    private JID newVenue_;
    private String reason_ = "";

    /**
     * Create the Destroy payload 
     */
    public MUCDestroyPayload() {
    }

    /**
     * Get new venue jabber ID
     * @return new venue, can be null if not set
     */
    public JID getNewVenue()  {
        return newVenue_;
    }

    /**
     * Get the reason
     * @return reason string, can be null if not set
     */
    public String getReason() {
        return reason_;
    }

    /**
     * Set the jabber ID for new Venue
     * @param jid jabber ID, not null
     */
    public void setNewVenue(JID jid) {
        newVenue_ = jid;
    }

    /**
     * Set the reason string
     * @param reason reason string, not null
     */
    public void setReason(String reason) {
        reason_ = reason;
    }
}
