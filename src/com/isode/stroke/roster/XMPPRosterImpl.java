/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.roster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.isode.stroke.elements.RosterItemPayload;
import com.isode.stroke.elements.RosterItemPayload.Subscription;
import com.isode.stroke.jid.JID;

public class XMPPRosterImpl extends XMPPRoster {
	
	private Map<JID, XMPPRosterItem> entries_ = new HashMap<JID, XMPPRosterItem>();

	public void addContact(final JID jid, final String name, final Collection<String> groups, RosterItemPayload.Subscription subscription) {
		JID bareJID = jid.toBare();
		XMPPRosterItem item = entries_.get(bareJID);

		if (item != null) {
			String oldName = item.getName();
			Collection<String> oldGroups = item.getGroups();
			entries_.put(bareJID, new XMPPRosterItem(jid, name, groups, subscription));
			onJIDUpdated.emit(bareJID, oldName, oldGroups);
		}
		else {
			entries_.put(bareJID, new XMPPRosterItem(jid, name, groups, subscription));
			onJIDAdded.emit(bareJID);
		}
	}

	public void removeContact(final JID jid) {
		entries_.remove(jid.toBare());
		onJIDRemoved.emit(jid);
	}

	public void clear() {
		entries_.clear();
		onRosterCleared.emit();
	}

	@Override
	public boolean containsJID(JID jid) {
		return entries_.containsKey(jid.toBare());
	}

	@Override
	public Subscription getSubscriptionStateForJID(JID jid) {
		XMPPRosterItem item = entries_.get(jid.toBare());
		if (item != null) return item.getSubscription();
		return RosterItemPayload.Subscription.None;
	}

	@Override
	public String getNameForJID(JID jid) {
		XMPPRosterItem item = entries_.get(jid.toBare());
		if (item != null) return item.getName();
		return "";
	}

	@Override
	public Collection<String> getGroupsForJID(JID jid) {
		XMPPRosterItem item = entries_.get(jid.toBare());
		if (item != null) return item.getGroups();
		return new ArrayList<String>();
	}

	@Override
	public Collection<XMPPRosterItem> getItems() {
		Collection<XMPPRosterItem> items = new ArrayList<XMPPRosterItem>(entries_.size());
		for (XMPPRosterItem item : entries_.values()) {
			items.add(new XMPPRosterItem(item));
		}
		return items;
	}

	@Override
	public XMPPRosterItem getItem(JID jid) {
		XMPPRosterItem item = entries_.get(jid.toBare());
		if (item != null) return item;

		return null;
	}

	@Override
	public Set<String> getGroups() {
		Set<String> groups = new HashSet<String>();
		for (XMPPRosterItem item : entries_.values())
			groups.addAll(item.getGroups());
		return groups;
	}
	
}
