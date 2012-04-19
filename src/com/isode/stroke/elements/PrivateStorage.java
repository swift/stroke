/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.elements;

/**
 * Class representing Private XML Storage for storing arbitrary private
 * user data on User's server(such as XEP-0048's bookmarks)
 *
 */
public class PrivateStorage extends Payload {
    
    /**
     * Constructor 
     * @param p payload, not null
     */
    public PrivateStorage(Payload p) {
        this.payload = p;
    }

    /**
     * Get payload
     * @return payload, not null
     */
    public Payload getPayload() {
        return payload;
    }

    /**
     * Set payload
     * @param p payload, not null
     */
    public void setPayload(Payload p) {
        payload = p;
    }

    private Payload payload;
}
