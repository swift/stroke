/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Kevin Smith
 * All rights reserved.
 */
package com.isode.stroke.elements;

import java.util.Vector;
import com.isode.stroke.jid.JID;

/**
 * Class representing a MUC User Payload
 *
 */
public class MUCUserPayload extends Payload {
    public static class StatusCode {
        public int code;
        public StatusCode()  {
            code = 0;
        }
        public StatusCode(int code) {
            this.code = code;
        }
    };

    /**
     * Class representing an Invite stanza.
     * Reason is optional while "from" and "to" are mutually exclusive.
     * "From" is used for MUC sending to invited client and is the JID 
     * the MUC claims the invite is from.
     * "To" is used sending to MUC from inviting client and is the 
     * JID to send the invite to.
     */
    public static class Invite {
        public JID from;
        public String reason;
        public JID to;
    };

    public Invite invite_;

    public String password_;

    public Payload payload_;

    public Vector<StatusCode> statusCodes_ = new Vector<StatusCode>();

    private Vector<MUCItem> items_ = new Vector<MUCItem>();

    /**
     * Constructor 
     */
    public MUCUserPayload() {
    }

    /**
     * Add a MUC Item
     * @param item item to be added, not null
     */
    public void addItem(MUCItem item) {
        items_.add(item);
    }

    /**
     * Add status code
     * @param code status code, not null
     */
    public void addStatusCode(StatusCode code) {
        statusCodes_.add(code);
    }

    /**
     * Get the invite object
     * @return invite object, null if not set
     */
    public Invite getInvite() {
        return invite_;
    }

    /**
     * Get the list of MUC items
     * @return list of MUC Items, can be empty but not null
     */
    public Vector<MUCItem> getItems() {
        return items_;
    }

    /**
     * Get the password for the room
     * @return room password, can be null
     */
    public String getPassword() {
        return password_;
    }

    /**
     * Get the payload
     * @return payload, null if not set
     */
    public Payload getPayload() {
        return payload_;
    }
    
    /**
     * Get the list of status codes
     * @return list of status codes, can be empty but not null
     */
    public Vector<StatusCode> getStatusCodes() {
        return statusCodes_;
    }
    
    /**
     * Set the invite value
     * @param invite invite value, not null
     */
    public void setInvite(Invite invite) {
        invite_ = invite;
    }
    
    /**
     * Set the password for the MUC
     * @param password password, can be null
     */
    public void setPassword(String password) {
        password_ = password;
    }
    
    /**
     * Set the payload
     * @param p payload, not null
     */
    public void setPayload(Payload p) {
        payload_ = p;
    }
}
