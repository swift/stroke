/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.presence;

import com.isode.stroke.client.StanzaChannel;
import com.isode.stroke.elements.Presence;
import com.isode.stroke.jid.JID;
import com.isode.stroke.signals.Signal2;
import com.isode.stroke.signals.Signal3;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot1;

public class SubscriptionManager {
	private StanzaChannel stanzaChannel;
	private SignalConnection onPresenceReceivedConnection;
	
	public final Signal3<JID, String, Presence> onPresenceSubscriptionRequest = new Signal3<JID, String, Presence>();

	public final Signal2<JID, String> onPresenceSubscriptionRevoked = new Signal2<JID, String>();

	public SubscriptionManager(StanzaChannel channel) {
		stanzaChannel = channel;
		onPresenceReceivedConnection = stanzaChannel.onPresenceReceived.connect(new Slot1<Presence>() {
				@Override
				public void call(Presence p1) {
					handleIncomingPresence(p1);
				}
			});
	}

	void delete() {
		onPresenceReceivedConnection.disconnect();
	}

	public void cancelSubscription(final JID jid) {
		Presence stanza = new Presence();
		stanza.setType(Presence.Type.Unsubscribed);
		stanza.setTo(jid);
		stanzaChannel.sendPresence(stanza);
	}

	public void confirmSubscription(final JID jid) {
		Presence stanza = new Presence();
		stanza.setType(Presence.Type.Subscribed);
		stanza.setTo(jid);
		stanzaChannel.sendPresence(stanza);
	}


	public void requestSubscription(final JID jid) {
		Presence stanza = new Presence();
		stanza.setType(Presence.Type.Subscribe);
		stanza.setTo(jid);
		stanzaChannel.sendPresence(stanza);
	}

	void handleIncomingPresence(Presence presence) {
		JID bareJID = presence.getFrom().toBare();
		if (presence.getType() == Presence.Type.Subscribe) {
			onPresenceSubscriptionRequest.emit(bareJID, presence.getStatus(), presence);
		}
		else if (presence.getType() == Presence.Type.Unsubscribe) {
			onPresenceSubscriptionRevoked.emit(bareJID, presence.getStatus());
		}
	}
}
