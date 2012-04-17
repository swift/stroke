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
 * Class representing a MUC Item
 *
 */
public class MUCItem {
    /**
     * Create the MUC Item
     */
    public MUCItem() {        
    }
    
    public JID realJID;
    public String nick;
    public MUCOccupant.Affiliation affiliation;
    public MUCOccupant.Role role;
    public JID actor;
    public String reason;
}
