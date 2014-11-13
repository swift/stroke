/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tron?on.
 * All rights reserved.
 */
package com.isode.stroke.elements;

import com.isode.stroke.jid.JID;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Roster Items
 */
public class RosterItemPayload {

    public enum Subscription {

        None, To, From, Both, Remove
    };

    public RosterItemPayload() {
    	jid_ = new JID();
    	name_ = "";
        subscription_ = Subscription.None;
        ask_ = false;
        groups_ = new ArrayList<String>();
    }

    public RosterItemPayload(JID jid, String name, Subscription subscription, Collection<String> groups) {
        jid_ = jid;
        name_ = name;
        subscription_ = subscription;
        ask_ = false;
        groups_ = groups;
    }

    public RosterItemPayload(JID jid, String name, Subscription subscription) {
    	this(jid, name, subscription, new ArrayList<String>());
    }

    public void setJID(JID jid) {
        jid_ = jid;
    }

    public JID getJID() {
        return jid_;
    }

    public void setName(String name) {
        name_ = name;
    }

    public String getName() {
        return name_;
    }

    public void setSubscription(Subscription subscription) {
        subscription_ = subscription;
    }

    public Subscription getSubscription() {
        return subscription_;
    }

    public void addGroup(String group) {
        groups_.add(group);
    }

    public void setGroups(Collection<String> groups) {
        groups_ = new ArrayList<String>();
        groups_.addAll(groups);
    }

    public Collection<String> getGroups() {
        return groups_;
    }

    public void setSubscriptionRequested() {
        ask_ = true;
    }

    public boolean getSubscriptionRequested() {
        return ask_;
    }
    private JID jid_;
    private String name_;
    private Subscription subscription_;
    private Collection<String> groups_;
    private boolean ask_;
}
