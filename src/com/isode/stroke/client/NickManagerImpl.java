/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.client;

import com.isode.stroke.elements.VCard;
import com.isode.stroke.jid.JID;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot2;
import com.isode.stroke.vcards.VCardManager;

public class NickManagerImpl extends NickManager {
	private JID ownJID = new JID();
	private String ownNick = "";
	private VCardManager vcardManager;
	private SignalConnection vCardChangedSignal;

	public NickManagerImpl(final JID ownJID, VCardManager vcardManager) {
		this.ownJID = ownJID;
		this.vcardManager = vcardManager;
		vCardChangedSignal = vcardManager.onVCardChanged.connect(new Slot2<JID, VCard>() {
			@Override
			public void call(JID p1, VCard p2) {
				handleVCardReceived(p1, p2);
			}
		});

		updateOwnNickFromVCard(vcardManager.getVCard(ownJID.toBare()));
	}

	public void delete() {
		vCardChangedSignal.disconnect();
	}

	@Override
	public String getOwnNick()  {
		return ownNick;
	}

	@Override
	public void setOwnNick(final String nick) {
	}

	void handleVCardReceived(final JID jid, VCard vcard) {
		if (jid.compare(ownJID, JID.CompareType.WithoutResource) != 0) {
			return;
		}
		updateOwnNickFromVCard(vcard);
	}

	void updateOwnNickFromVCard(VCard vcard) {
		String nick = null;
		if (vcard != null && !vcard.getNickname().isEmpty()) {
			nick = vcard.getNickname();
		}
		if (nick != ownNick && nick != null && !nick.equals(ownNick)) {
			ownNick = nick;
			onOwnNickChanged.emit(ownNick);
		}
	}

}
