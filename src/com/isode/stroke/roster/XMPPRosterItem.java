/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.roster;

import java.util.Collection;
import java.util.ArrayList;

import com.isode.stroke.elements.RosterItemPayload;
import com.isode.stroke.jid.JID;

public class XMPPRosterItem {
    private JID jid;
    private String name;
    private Collection<String> groups;
    private RosterItemPayload.Subscription subscription;

    public XMPPRosterItem(final JID jid, final String name, final Collection<String> groups, RosterItemPayload.Subscription subscription) {
        this.jid = jid;
        this.name = name;
        this.groups = groups;
        this.subscription = subscription;
    }
    
    // Copy constructor
    public XMPPRosterItem(XMPPRosterItem from) {
    	this(from.jid, from.name, null, from.subscription);
    	if (from.groups != null) {
    		groups = new ArrayList<String>(from.groups.size());
    		groups.addAll(from.groups);
    	}
    }

    public final JID getJID() {
        return jid;
    }

    public final String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public final Collection<String> getGroups() {
        return groups;
    }

    public void setGroups(final Collection<String> groups) {
        this.groups = groups;
    }

    public RosterItemPayload.Subscription getSubscription() {
        return subscription;
    }
}
