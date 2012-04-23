/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon
 * All rights reserved.
 */
package com.isode.stroke.muc;

import java.util.Vector;

import com.isode.stroke.jid.JID;

/**
 * Class representing a MUC Registry
 *
 */
public class MUCRegistry {   

    /**
     * Add the JID of a multi-user chat room to the registry
     * @param j JID of the room, not null
     */
    public void addMUC(JID j) {
        mucs.add(j);
    }

    /**
     * Determine if the given JID is contained in the Jabber ID
     * @param j Jabber ID, not null
     * @return true if it exists in the Registry and false otherwise
     */
    public boolean isMUC(JID j){
        return mucs.contains(j);
    }

    /**
     * Remove the Jabber ID from the registry
     * @param j Jabber ID to remove, not null
     */
    public void removeMUC(JID j) {
        mucs.remove(j);
    }

    private Vector<JID> mucs = new Vector<JID>();
}
