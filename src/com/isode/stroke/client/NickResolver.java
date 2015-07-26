/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.client;

import java.util.Collection;

import com.isode.stroke.elements.VCard;
import com.isode.stroke.jid.JID;
import com.isode.stroke.muc.MUCRegistry;
import com.isode.stroke.roster.XMPPRoster;
import com.isode.stroke.signals.Signal2;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.signals.Slot2;
import com.isode.stroke.signals.Slot3;
import com.isode.stroke.vcards.VCardManager;

// FIXME: The NickResolver currently relies on the vcard being requested by the client on login.
// The VCardManager should get an onConnected() signal (which is signalled when the stanzachannel is available(, and each time this is emitted,
// the nickresolver should request the vcard.
// FIXME: The ownJID functionality should probably be removed, and NickManager should be used directly.

public class NickResolver {
	private JID ownJID_ = new JID();
	private String ownNick_ = "";
	private XMPPRoster xmppRoster_;
	private MUCRegistry mucRegistry_;
	private VCardManager vcardManager_;

	public final Signal2<JID, String /*previousNick*/ > onNickChanged = new Signal2<JID, String>();

	public NickResolver(final JID ownJID, XMPPRoster xmppRoster, VCardManager vcardManager, MUCRegistry mucRegistry) {
		ownJID_ = ownJID;
		xmppRoster_ = xmppRoster;
		vcardManager_ = vcardManager;
		if (vcardManager_ != null) {
			vcardManager_.onVCardChanged.connect(new Slot2<JID, VCard>() {
				@Override
				public void call(JID p1, VCard p2) {
					handleVCardReceived(p1, p2);
				}
			});
		}
		mucRegistry_ = mucRegistry;
		xmppRoster_.onJIDUpdated.connect(new Slot3<JID, String, Collection<String>>() {
				@Override
				public void call(JID p1, String p2, Collection<String> p3) {
					handleJIDUpdated(p1, p2, p3);
				}
			});
		xmppRoster_.onJIDAdded.connect(new Slot1<JID>() {
				@Override
				public void call(JID p1) {
					handleJIDAdded(p1);
				}
			});
	}

	void handleJIDUpdated(final JID jid, final String previousNick, final Collection<String> groups) {
		onNickChanged.emit(jid, previousNick);
	}

	void handleJIDAdded(final JID jid) {
		String oldNick = jidToNick(jid);
		onNickChanged.emit(jid, oldNick);
	}

	public String jidToNick(final JID jid) {
		if (jid.toBare().equals(ownJID_)) {
			if (ownNick_ != null && !ownNick_.isEmpty()) {
				return ownNick_;
			}
		}
		String nick = "";
		if (mucRegistry_ != null && mucRegistry_.isMUC(jid.toBare()) ) {
			return jid.getResource().isEmpty() ? jid.toBare().toString() : jid.getResource();
		}

		if (xmppRoster_.containsJID(jid) && !xmppRoster_.getNameForJID(jid).isEmpty()) {
			return xmppRoster_.getNameForJID(jid);
		}

		return jid.toBare().toString();
	}

	void handleVCardReceived(final JID jid, VCard ownVCard) {
		if (jid.compare(ownJID_, JID.CompareType.WithoutResource) != 0) {
			return;
		}
		String initialNick = ownNick_;
		ownNick_ = ownJID_.toString();
		if (ownVCard != null) {
			if (!ownVCard.getNickname().isEmpty()) {
				ownNick_ = ownVCard.getNickname();
			} else if (!ownVCard.getGivenName().isEmpty()) {
				ownNick_ = ownVCard.getGivenName();
			} else if (!ownVCard.getFullName().isEmpty()) {
				ownNick_ = ownVCard.getFullName();
			}
		}
	}

}
