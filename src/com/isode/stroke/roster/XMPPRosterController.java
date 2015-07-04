/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.roster;

import java.util.Collection;

import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.elements.RosterItemPayload;
import com.isode.stroke.elements.RosterPayload;
import com.isode.stroke.roster.GetRosterRequest;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.signals.Slot2;

public class XMPPRosterController {
	private IQRouter iqRouter_;
	private RosterPushResponder rosterPushResponder_;
	private XMPPRosterImpl xmppRoster_;
	private RosterStorage rosterStorage_;
	private boolean useVersioning;


	/**
	 * The controller does not gain ownership of these parameters.
	 */
	public XMPPRosterController(IQRouter iqRouter, XMPPRosterImpl xmppRoster, RosterStorage rosterStorage) {
		iqRouter_ = iqRouter;
		rosterPushResponder_ = new RosterPushResponder(iqRouter);
		xmppRoster_ = xmppRoster;
		rosterStorage_ = rosterStorage;
		useVersioning = false;
		
		rosterPushResponder_.onRosterReceived.connect(new Slot1<RosterPayload>() {
				@Override
				public void call(RosterPayload p1) {
					handleRosterReceived(p1, false, new RosterPayload());
				}
			});
		rosterPushResponder_.start();
	}

	public void delete() {
		rosterPushResponder_.stop();
	}

	public void requestRoster() {
		xmppRoster_.clear();

		final RosterPayload storedRoster = rosterStorage_.getRoster();
		GetRosterRequest rosterRequest;
		if (useVersioning) {
			String version = "";
			if (storedRoster != null && storedRoster.getVersion() != null) {
				version = storedRoster.getVersion();
			}
			rosterRequest = GetRosterRequest.create(iqRouter_, version);
		}
		else {
			rosterRequest = GetRosterRequest.create(iqRouter_);
		}
		
		rosterRequest.onResponse.connect(new Slot2<RosterPayload, ErrorPayload>() {
				@Override
				public void call(RosterPayload p1, ErrorPayload p2) {
					handleRosterReceived(p1, true, storedRoster);
				}
			});
		rosterRequest.send();
	}

	private void handleRosterReceived(RosterPayload rosterPayload, boolean initial, RosterPayload previousRoster) {
		if (rosterPayload != null) {
			for (final RosterItemPayload item : rosterPayload.getItems()) {
				//Don't worry about the updated case, the XMPPRoster sorts that out.
				if (item.getSubscription() == RosterItemPayload.Subscription.Remove) {
					xmppRoster_.removeContact(item.getJID());
				} else {
					xmppRoster_.addContact(item.getJID(), item.getName(), item.getGroups(), item.getSubscription());
				}
			}
		}
		else if (previousRoster != null) {
			// The cached version hasn't changed; emit all items
			for (final RosterItemPayload item : previousRoster.getItems()) {
				if (item.getSubscription() != RosterItemPayload.Subscription.Remove) {
					xmppRoster_.addContact(item.getJID(), item.getName(), item.getGroups(), item.getSubscription());
				}
				else {
					System.err.println("ERROR: Stored invalid roster item");
				}
			}
		}
		if (initial) {
			xmppRoster_.onInitialRosterPopulated.emit();
		}
		if (rosterPayload != null && rosterPayload.getVersion() != null && useVersioning) {
			saveRoster(rosterPayload.getVersion());
		}
	}

	private void saveRoster(final String version) {
		Collection<XMPPRosterItem> items = xmppRoster_.getItems();
		RosterPayload roster = new RosterPayload();
		roster.setVersion(version);
		for (final XMPPRosterItem item : items) {
			roster.addItem(new RosterItemPayload(item.getJID(), item.getName(), item.getSubscription(), item.getGroups()));
		}
		rosterStorage_.setRoster(roster);
	}

	public void setUseVersioning(boolean b) {
		useVersioning = b;
	}
}
