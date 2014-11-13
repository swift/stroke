/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.roster;

import java.util.Set;
import java.util.Collection;

import com.isode.stroke.elements.RosterItemPayload;
import com.isode.stroke.jid.JID;
import com.isode.stroke.signals.Signal;
import com.isode.stroke.signals.Signal1;
import com.isode.stroke.signals.Signal3;

public abstract class XMPPRoster {
	/**
	 * Checks whether the bare jid of the given jid is in the roster.
	 */
	public abstract boolean containsJID(final JID jid); 

	/**
	 * Retrieves the subscription state for the given jid.
	 */
	public abstract RosterItemPayload.Subscription getSubscriptionStateForJID(final JID jid); 

	/**
	 * Retrieves the stored roster name for the given jid.
	 */
	public abstract String getNameForJID(final JID jid); 

	/**
	 * Returns the list of groups for the given JID.
	 */
	public abstract Collection<String> getGroupsForJID(final JID jid); 

	/**
	 * Retrieve the items in the roster.
	 */
	public abstract Collection<XMPPRosterItem> getItems(); 

	/**
	 * Retrieve the item with the given JID.
	 */
	public abstract XMPPRosterItem getItem(final JID jid);

	/**
	 * Retrieve the list of (existing) groups.
	 */
	public abstract Set<String> getGroups(); 

	/**
	 * Emitted when the given JID is added to the roster.
	 */
	public final Signal1<JID> onJIDAdded = new Signal1<JID>();

	/**
	 * Emitted when the given JID is removed from the roster.
	 */
	public final Signal1<JID> onJIDRemoved = new Signal1<JID>();

	/**
	 * Emitted when the name or the groups of the roster item with the
	 * given JID changes.
	 */
	public final Signal3<JID, String, Collection<String>> onJIDUpdated = new Signal3<JID, String, Collection<String>>();

	/**
	 * Emitted when the roster is reset (e.g. due to logging in/logging out).
	 * After this signal is emitted, the roster is empty. It will be repopulated through
	 * onJIDAdded and onJIDRemoved events.
	 */
	public final Signal onRosterCleared = new Signal();

	/**
	 * Emitted after the last contact of the initial roster request response
	 * was added.
	 */
	public final Signal onInitialRosterPopulated = new Signal();
}
