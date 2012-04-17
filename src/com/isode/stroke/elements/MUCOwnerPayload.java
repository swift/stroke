/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Kevin Smith
 * All rights reserved.
 */
package com.isode.stroke.elements;

/**
 * Class representing a MUC Owner Payload
 *
 */
public class MUCOwnerPayload extends Payload {

    /**
     * Create the MUC Owner payload
     */
    public MUCOwnerPayload() {
    }

    /**
     * Get the payload
     * @return payload, not null if set
     */
    public Payload getPayload(){
        return payload;
    }

    /**
     * Set the payload
     * @param p payload to set, nt null
     */
    public void setPayload(Payload p) {
        payload = p;
    }

    /**
     * Get the form object
     * @return form, not null if payload is set to Form
     */
    public Form getForm() {
        if(payload instanceof Form) {
            return (Form)payload;
        }
        return null;
    }

    private Payload payload;
}
