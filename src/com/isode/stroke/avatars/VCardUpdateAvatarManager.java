/*
 * Copyright (c) 2010-2015 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.avatars;

import java.util.Map;
import java.util.HashMap;

import com.isode.stroke.avatars.AvatarProvider;
import com.isode.stroke.jid.JID;
import com.isode.stroke.elements.VCard;
import com.isode.stroke.elements.Presence;
import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.elements.VCardUpdate;
import com.isode.stroke.client.StanzaChannel;
import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.stringcodecs.Hexify;
import com.isode.stroke.avatars.AvatarStorage;
import com.isode.stroke.muc.MUCRegistry;
import com.isode.stroke.vcards.VCardManager;
import com.isode.stroke.signals.Slot2;
import com.isode.stroke.signals.Slot1;

import java.util.logging.Logger;
import com.isode.stroke.signals.SignalConnection;

public class VCardUpdateAvatarManager extends AvatarProvider {

	private VCardManager vcardManager_;
	private AvatarStorage avatarStorage_;
	private CryptoProvider crypto_;
	private MUCRegistry mucRegistry_;
	private final Map<JID, String> avatarHashes_ = new HashMap<JID, String>();
	private final SignalConnection onPresenceReceivedConnection;
	private final SignalConnection onAvailableChangedConnection;
	private final SignalConnection onVCardChangedConnection;
	private final Logger logger_ = Logger.getLogger(this.getClass().getName());

	public VCardUpdateAvatarManager(VCardManager vcardManager, StanzaChannel stanzaChannel, AvatarStorage avatarStorage, CryptoProvider crypto) {
		this(vcardManager, stanzaChannel, avatarStorage, crypto, null);
	}

	public VCardUpdateAvatarManager(VCardManager vcardManager, StanzaChannel stanzaChannel, AvatarStorage avatarStorage, CryptoProvider crypto, MUCRegistry mucRegistry) {
		this.vcardManager_ = vcardManager;
		this.avatarStorage_ = avatarStorage;
		this.crypto_ = crypto;
		this.mucRegistry_ = mucRegistry;
		onPresenceReceivedConnection = stanzaChannel.onPresenceReceived.connect(new Slot1<Presence>() {
			@Override
			public void call(Presence p1) {
				handlePresenceReceived(p1);
			}
		});
		onAvailableChangedConnection = stanzaChannel.onAvailableChanged.connect(new Slot1<Boolean>() {
			@Override
			public void call(Boolean b) {
				handleStanzaChannelAvailableChanged(b);
			}
		});
		onVCardChangedConnection = vcardManager_.onVCardChanged.connect(new Slot2<JID, VCard>() {
			@Override
			public void call(JID p1, VCard vcard) {
				handleVCardChanged(p1, vcard);
			}
		});
	}
	
	@Override
	public void delete() {
		onPresenceReceivedConnection.disconnect();
		onAvailableChangedConnection.disconnect();
		onVCardChangedConnection.disconnect();
	}

	@Override
	public String getAvatarHash(JID jid) {
		if(avatarHashes_.containsKey(jid)) {
			return avatarHashes_.get(jid);
		} else {
			return null;
		}
	}

	private void handlePresenceReceived(Presence presence) {
		VCardUpdate update = presence.getPayload(new VCardUpdate());
		if (update == null || presence.getPayload(new ErrorPayload()) != null) {
			return;
		}
		JID from = getAvatarJID(presence.getFrom());
		if (update.getPhotoHash().equals(getAvatarHash(from))) {
			return;
		}
		logger_.fine("Updated hash: " + from + "-> " + update.getPhotoHash() + "\n");
		if (avatarStorage_.hasAvatar(update.getPhotoHash())) {
			setAvatarHash(from, update.getPhotoHash());
		}
		else {
			vcardManager_.requestVCard(from);
		}
	}

	private void handleStanzaChannelAvailableChanged(boolean available) {
		if (available) {
			Map<JID, String> oldAvatarHashes = new HashMap<JID, String>();
			oldAvatarHashes.putAll(avatarHashes_);
			avatarHashes_.clear();
			for (Map.Entry<JID, String> entry : oldAvatarHashes.entrySet()) {
				onAvatarChanged.emit(entry.getKey());
			}
		}
	}

	private void handleVCardChanged(JID from, VCard vCard) {
		if (vCard == null) {
			logger_.fine("Missing element: " + from + ": null vcard payload\n");
			return;
		}

		if (vCard.getPhoto().isEmpty()) {
			setAvatarHash(from, "");
		}
		else {
			String hash = Hexify.hexify(crypto_.getSHA1Hash(vCard.getPhoto()));
			if (!avatarStorage_.hasAvatar(hash)) {
				avatarStorage_.addAvatar(hash, vCard.getPhoto());
			}
			setAvatarHash(from, hash);
		}
	}

	private void setAvatarHash(JID from, String hash) {
		logger_.fine("Updating hash: " + from + " -> " + hash + "\n");
		avatarHashes_.put(from, hash);
		onAvatarChanged.emit(from);
	}

	private JID getAvatarJID(JID jid) {
		JID bareFrom = jid.toBare();
		return (mucRegistry_ != null && mucRegistry_.isMUC(bareFrom)) ? jid : bareFrom;
	}
}
