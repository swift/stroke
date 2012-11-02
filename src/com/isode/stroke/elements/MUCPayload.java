/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Kevin Smith
 * All rights reserved.
 */
package com.isode.stroke.elements;

import java.util.Date;

/**
 * Class representing a MUC Payload
 *
 */
public class MUCPayload extends Payload {
    private int maxChars_;
    private int maxStanzas_;
    private int seconds_;
    private Date since_;
    private String password_;

    /**
     * Constructor 
     */
    public MUCPayload() {
        maxChars_ = -1;
        maxStanzas_ = -1;
        seconds_ = -1;
    }

    /**
     * Set the maximum number of characters where character count is the characters of the 
     * complete XML stanzas, not only their XML character data
     * @param maxChars maximum number of characters (positive value)
     */
    public void setMaxChars(int maxChars) {
        maxChars_ = maxChars;
    }

    /**
     * Set the maximum number of stanzas which means limiting the total number of messages
     * @param maxStanzas maximum number of stanzas 
     */
    public void setMaxStanzas(int maxStanzas) {
        maxStanzas_ = maxStanzas;
    }

    /**
     * Set the number of seconds which means send only the messages received in the 
     * last "X" seconds.
     * @param seconds number of seconds
     */
    public void setSeconds(int seconds) {
        seconds_ = seconds;
    }

    /**
     * Set the date which means send only the messages received since the 
     * date/time specified
     * @param since date-time, should not be null
     */
    public void setSince(Date since) {
        since_ = (Date)since.clone();
    }

    /**
     * Set the MUC password
     * @param password password, can be null
     */
    public void setPassword(String password) {
        password_ = password;
    }

    /**
     * Get the maximum number of characters
     * @return max characters
     */
    public int getMaxChars() {
        return maxChars_;
    }

    /**
     * Get the maximum number of stanzas
     * @return max stanzas
     */
    public int getMaxStanzas(){
        return maxStanzas_;
    }

    /**
     * Get the number of seconds
     * @return number of seconds
     */
    public int getSeconds() {
        return seconds_;
    }

    /**
     * Get the password
     * @return password, can be null if not set
     */
    public String getPassword() {
        return password_;
    }

    /**
     * Get the date specified to limit the stazas
     * @return date, ca be null if not set
     */
    public Date getSince() {
        if(since_ == null) {
            return null;
        }
        return (Date)since_.clone();
    }
}
