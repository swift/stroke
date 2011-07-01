/*
 * Copyright (c) 2010, Isode Limited, London, England.
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
        subscription_ = Subscription.None;
        ask_ = false;
    }

    public RosterItemPayload(JID jid, String name, Subscription subscription) {
        jid_ = jid;
        name_ = name;
        subscription_ = subscription;
        ask_ = false;
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
    private ArrayList<String> groups_ = new ArrayList<String>();
    private boolean ask_;
}
