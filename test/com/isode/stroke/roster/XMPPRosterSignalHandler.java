/*
 * Copyright (c) 2010-2011 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.roster;

import com.isode.stroke.roster.XMPPRosterImpl;
import com.isode.stroke.roster.XMPPRosterEvents;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.signals.Slot3;
import com.isode.stroke.jid.JID;
import java.util.Collection;
import java.util.ArrayList;

enum XMPPRosterEvents {
	None,
	Add, 
	Remove,
	Update
};

public class XMPPRosterSignalHandler {

	public XMPPRosterSignalHandler(XMPPRoster roster) {
		this.eventCount = 0;
		lastEvent_ = XMPPRosterEvents.None;
		onJIDAddedConnection = roster.onJIDAdded.connect(new Slot1<JID>() {
			@Override
			public void call(JID j1) {
				handleJIDAdded(j1);
			}
		});

		onJIDRemovedConnection = roster.onJIDRemoved.connect(new Slot1<JID>() {
			@Override
			public void call(JID j1) {
				handleJIDRemoved(j1);
			}
		});

		onJIDUpdatedConnection = roster.onJIDUpdated.connect(new Slot3<JID, String, Collection<String> >() {
			@Override
			public void call(JID j1, String s1, Collection<String> c1) {
				handleJIDUpdated(j1, s1, c1);
			}
		});
	}

	public XMPPRosterEvents getLastEvent() {
		return lastEvent_;
	}

	public JID getLastJID() {
		return lastJID_;
	}

	public String getLastOldName() {
		return lastOldName_;
	}

	public Collection<String> getLastOldGroups() {
		return lastOldGroups_;
	}

	public void reset() {
		lastEvent_ = XMPPRosterEvents.None;
	}

	public final int getEventCount() {
		return eventCount;
	}

	private void handleJIDAdded(final JID jid) {
		lastJID_ = jid;
		lastEvent_ = XMPPRosterEvents.Add;
		eventCount++;
	}

	private void handleJIDRemoved(final JID jid) {
		lastJID_ = jid;
		lastEvent_ = XMPPRosterEvents.Remove;
		eventCount++;
	}

	private void handleJIDUpdated(final JID jid, final String oldName, final Collection<String> oldGroups) {
		assert(lastEvent_ == XMPPRosterEvents.None);
		lastJID_ = jid;
		lastOldName_ = oldName;
		lastOldGroups_ = oldGroups;
		lastEvent_ = XMPPRosterEvents.Update;
		eventCount++;
	}

	private XMPPRosterEvents lastEvent_;
	private JID lastJID_ = new JID(); //initialized
	private String lastOldName_ = "";
	private Collection<String> lastOldGroups_ = new ArrayList<String>(); //initialized
	private int eventCount;
	private SignalConnection onJIDAddedConnection;
	private SignalConnection onJIDRemovedConnection;
	private SignalConnection onJIDUpdatedConnection;
}