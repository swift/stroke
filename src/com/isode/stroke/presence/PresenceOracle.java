/*
 * Copyright (c) 2010-2016, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.presence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.isode.stroke.client.StanzaChannel;
import com.isode.stroke.elements.Presence;
import com.isode.stroke.elements.StatusShow;
import com.isode.stroke.jid.JID;
import com.isode.stroke.roster.XMPPRoster;
import com.isode.stroke.signals.Signal1;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot1;

public class PresenceOracle {
	private final Map<JID,Map<JID,Presence>> entries_ = new HashMap<JID,Map<JID,Presence>>();
	private final StanzaChannel stanzaChannel_;
	private final SignalConnection onPresenceReceivedSignal;
	private final SignalConnection onAvailableChangedSignal;
	private final XMPPRoster xmppRoster_;


	public final Signal1<Presence> onPresenceChange = new Signal1<Presence>();
    private final SignalConnection onJIDRemovedConnection;

	public PresenceOracle(StanzaChannel stanzaChannel, XMPPRoster xmppRoster) {
	    stanzaChannel_ = stanzaChannel;
	    xmppRoster_ = xmppRoster;
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
	    onJIDRemovedConnection = xmppRoster_.onJIDRemoved.connect(new Slot1<JID>() {

	        @Override
	        public void call(JID removedJID) {
	            handleJIDRemoved(removedJID);
	        }

	    });
	}

	void delete() {
		onPresenceReceivedSignal.disconnect();
		onAvailableChangedSignal.disconnect();
		onJIDRemovedConnection.disconnect();
	}

	void handleStanzaChannelAvailableChanged(boolean available) {
		if (available) {
			entries_.clear();
		}
	}


	void handleIncomingPresence(Presence presence) {
		JID bareJID = presence.getFrom().toBare();
		if (Presence.Type.Subscribe.equals(presence.getType())) {
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
			if (passedPresence.getFrom().isBare() && Presence.Type.Unavailable.equals(presence.getType())) {
				/* Have a bare-JID only presence of offline */
				jidMap.clear();
			} else if (Presence.Type.Available.equals(passedPresence.getType())) {
				/* Don't have a bare-JID only offline presence once there are available presences */
				jidMap.remove(bareJID);
			}
			if (Presence.Type.Unavailable.equals(passedPresence.getType()) && jidMap.size() > 1) {
				jidMap.remove(passedPresence.getFrom());
			} else {
				jidMap.put(passedPresence.getFrom(), passedPresence);
			}
			entries_.put(bareJID, jidMap);
			onPresenceChange.emit(passedPresence);
		}
	}
	
	private void handleJIDRemoved(JID removedJID) {
	    // 3921bis says that we don't follow up with an unavailable, so simulate this ourselves
	    Presence unavailablePresence = new Presence();
	    unavailablePresence.setType(Presence.Type.Unavailable);
	    unavailablePresence.setFrom(removedJID);

	    if (entries_.containsKey(removedJID.toBare())) {
	        Map<JID,Presence> presenceMap = entries_.get(removedJID.toBare());
	        presenceMap.clear();
	        presenceMap.put(removedJID, unavailablePresence);
	    }

	    onPresenceChange.emit(unavailablePresence);
	}

	public Presence getLastPresence(final JID jid) {
		Map<JID,Presence> presenceMap = entries_.get(jid.toBare());
		if (presenceMap == null) return null;
		
		Presence i = presenceMap.get(jid);
		if (i != null) {
			return i;
		} else {
			return null;
		}
	}

	public Collection<Presence> getAllPresence(final JID bareJID) {
		Collection<Presence> results = new ArrayList<Presence>();
		
		Map<JID,Presence> presenceMap = entries_.get(bareJID);
		if (presenceMap == null) return results;
		
		results.addAll(presenceMap.values());
		return results;
	}
	
	private static class PresenceAccountCmp implements Comparator<Presence> {

	    private static int preferenceFromStatusShow(StatusShow.Type showType) {
	        switch (showType) {
	        case FFC:
                return 5;
	        case Online:
                return 4;
	        case DND:
                return 3;
            case Away:
                return 2;
            case XA:
                return 1;
            case None:
                return 0;
	        }
	        assert(false);
	        return -1;
	    }

	    @Override
	    public int compare(Presence a, Presence b) {
	        int aPreference = preferenceFromStatusShow(a.getShow());
	        int bPreference = preferenceFromStatusShow(b.getShow());
	        
	        if (aPreference != bPreference) {
	            return (aPreference > bPreference) ? -1 : 1;
	        }
	        if (a.getPriority() != b.getPriority()) {
	            return (a.getPriority() > b.getPriority()) ? -1 : 1;
	        }
	        return -a.getFrom().getResource().compareTo(b.getFrom().getResource());
	    }

	}
	
	/**
     * Returns the relevant presence for a list of resource presences.
     *
     * It only takes the presence show type into account. Priorities are
     * ignored as various clients set them to arbitrary values unrelated
     * to actual end point availability.
     *
     * The presences of the resources are group by availablilty and sorted
     * by show type in the following order:
     *
     * -# Online
     *    -# Free for Chat
     *    -# Available
     * -# Away
     *    -# DND
     *    -# Extended Away
     *    -# Away
     * -# Offline
     *    -# Unavailable
	 * @param presences List of resource presences.  Should not be null.
	 * @return The relevant presence.
     */
	public static Presence getActivePresence(Collection<? extends Presence> presences) {
        
	    PriorityQueue<Presence> online = new PriorityQueue<Presence>(presences.size(),new PresenceAccountCmp());
	    PriorityQueue<Presence> away = new PriorityQueue<Presence>(presences.size(),new PresenceAccountCmp());
	    PriorityQueue<Presence> offline = new PriorityQueue<Presence>(presences.size(),new PresenceAccountCmp());

	    for (Presence presence : presences) {
	        switch (presence.getShow()) {
	        case Online:
	            online.add(presence);
	            break;
	        case Away:
	            away.add(presence);
	            break;
	        case FFC:
                online.add(presence);
                break;
	        case XA:
	            away.add(presence);
                break;
	        case DND:
	            away.add(presence);
	            break;
	        case None:
	            offline.add(presence);
	            break;
	        }
	    }

	    Presence accountPresence = null;
	    if (!online.isEmpty()) {
	        accountPresence = online.peek();
	    }
	    else if (!away.isEmpty()) {
	        accountPresence = away.peek();
	    }
	    else if (!offline.isEmpty()) {
	        accountPresence = offline.peek();
	    }
	    return accountPresence;
	}
	
	/**
     * This considers all online resources of a bare JID and returns
     * the value returned by {@link #getActivePresence(List)} 
     * when passing this list.
	 * @param jid A bare JID
	 * @return The value returned by {@link #getActivePresence(List)} 
     */
    public Presence getAccountPresence(JID jid) {
        Collection<Presence> allPresences = getAllPresence(jid.toBare());
        Presence accountPresence = getActivePresence(allPresences);
        return accountPresence;
    }

	public Presence getHighestPriorityPresence(final JID bareJID) {
		Map<JID,Presence> presenceMap = entries_.get(bareJID);
		if (presenceMap == null) return null;

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
