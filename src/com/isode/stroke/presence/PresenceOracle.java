/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.presence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.isode.stroke.client.StanzaChannel;
import com.isode.stroke.elements.Presence;
import com.isode.stroke.elements.StatusShow;
import com.isode.stroke.jid.JID;
import com.isode.stroke.signals.Signal1;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot1;

public class PresenceOracle {
	private final Map<JID,Map<JID,Presence>> entries_ = new HashMap<JID,Map<JID,Presence>>();
	private final StanzaChannel stanzaChannel_;
	private final SignalConnection onPresenceReceivedSignal;
	private final SignalConnection onAvailableChangedSignal;


	public final Signal1<Presence> onPresenceChange = new Signal1<Presence>();

	public PresenceOracle(StanzaChannel stanzaChannel) {
		stanzaChannel_ = stanzaChannel;
		onPresenceReceivedSignal = stanzaChannel_.onPresenceReceived.connect(new Slot1<Presence>() {
				@Override
				public void call(Presence p1) {
					handleIncomingPresence(p1);
				}
			});
		onAvailableChangedSignal = stanzaChannel_.onAvailableChanged.connect(new Slot1<Boolean>() {
				@Override
				public void call(Boolean p1) {
					handleStanzaChannelAvailableChanged(p1);
				}
			});
	}

	void delete() {
		onPresenceReceivedSignal.disconnect();
		onAvailableChangedSignal.disconnect();
	}

	void handleStanzaChannelAvailableChanged(boolean available) {
		if (available) {
			entries_.clear();
		}
	}


	void handleIncomingPresence(Presence presence) {
		JID bareJID = presence.getFrom().toBare();
		if (presence.getType() == Presence.Type.Subscribe) {
		}
		else {
			Presence passedPresence = presence;
			if (presence.getType() == Presence.Type.Unsubscribe) {
				/* 3921bis says that we don't follow up with an unavailable, so simulate this ourselves */
				passedPresence = new Presence();
				passedPresence.setType(Presence.Type.Unavailable);
				passedPresence.setFrom(bareJID);
				passedPresence.setStatus(presence.getStatus());
			}
			Map<JID,Presence> jidMap = entries_.get(bareJID);
			if (jidMap == null) jidMap = new HashMap<JID,Presence>();
			if (passedPresence.getFrom().isBare() && presence.getType() == Presence.Type.Unavailable) {
				/* Have a bare-JID only presence of offline */
				jidMap.clear();
			} else if (passedPresence.getType() == Presence.Type.Available) {
				/* Don't have a bare-JID only offline presence once there are available presences */
				jidMap.remove(bareJID);
			}
			if (passedPresence.getType() == Presence.Type.Unavailable && jidMap.size() > 1) {
				jidMap.remove(passedPresence.getFrom());
			} else {
				jidMap.put(passedPresence.getFrom(), passedPresence);
			}
			entries_.put(bareJID, jidMap);
			onPresenceChange.emit(passedPresence);
		}
	}

	public Presence getLastPresence(final JID jid) {
		Map<JID,Presence> presenceMap = entries_.get(jid.toBare());
		if (presenceMap == null) return new Presence();
		
		Presence i = presenceMap.get(jid);
		if (i != null) {
			return i;
		} else {
			return new Presence();
		}
	}

	public Collection<Presence> getAllPresence(final JID bareJID) {
		Collection<Presence> results = new ArrayList<Presence>();
		
		Map<JID,Presence> presenceMap = entries_.get(bareJID);
		if (presenceMap == null) return results;
		
		results.addAll(presenceMap.values());
		return results;
	}

	public Presence getHighestPriorityPresence(final JID bareJID) {
		Map<JID,Presence> presenceMap = entries_.get(bareJID);
		if (presenceMap == null) return new Presence();

		Presence highest = null;
		for (Presence current : presenceMap.values()) {
			if (highest == null
					|| current.getPriority() > highest.getPriority()
					|| (current.getPriority() == highest.getPriority()
							&& StatusShow.typeToAvailabilityOrdering(current.getShow()) > StatusShow.typeToAvailabilityOrdering(highest.getShow()))) {
				highest = current;
			}

		}
		return highest;
	}
}
