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

import com.isode.stroke.avatars.AvatarProvider;
import com.isode.stroke.jid.JID;
import com.isode.stroke.elements.VCard;
import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.stringcodecs.Hexify;
import com.isode.stroke.avatars.AvatarStorage;
import com.isode.stroke.base.ByteArray;
import com.isode.stroke.muc.MUCRegistry;
import com.isode.stroke.vcards.VCardManager;
import com.isode.stroke.signals.Slot2;

import java.util.logging.Logger;

import com.isode.stroke.signals.SignalConnection;

public class VCardAvatarManager extends AvatarProvider {

	private VCardManager vcardManager_;
	private AvatarStorage avatarStorage_;
	private CryptoProvider crypto_;
	private MUCRegistry mucRegistry_;
	private final SignalConnection onVCardChangedConnection_;
	private final Logger logger_ = Logger.getLogger(this.getClass().getName());

	public VCardAvatarManager(VCardManager vcardManager, AvatarStorage avatarStorage, CryptoProvider crypto) {
		this(vcardManager, avatarStorage, crypto, null);
	}

	public VCardAvatarManager(VCardManager vcardManager, AvatarStorage avatarStorage, CryptoProvider crypto, MUCRegistry mucRegistry) {
		this.vcardManager_ = vcardManager;
		this.avatarStorage_ = avatarStorage;
		this.crypto_ = crypto;
		this.mucRegistry_ = mucRegistry;
		onVCardChangedConnection_ = vcardManager.onVCardChanged.connect(new Slot2<JID, VCard>() {
			@Override
			public void call(JID p1, VCard vcard) {
				handleVCardChanged(p1);
			}	
		});
	}

	@Override
	public void delete() {
		onVCardChangedConnection_.disconnect();
	}

	@Override
	public String getAvatarHash(JID jid) {
		JID avatarJID = getAvatarJID(jid);
		String hash = vcardManager_.getPhotoHash(avatarJID);
		if(hash.length() != 0) {
			if (!avatarStorage_.hasAvatar(hash)) {
				final VCard vCard = vcardManager_.getVCard(avatarJID);
				final ByteArray photo = vCard != null ? vCard.getPhoto() : null;
				if (photo != null) {
					String newHash = Hexify.hexify(crypto_.getSHA1Hash(photo));
					if (!newHash.equals(hash)) {
						// Shouldn't happen, but sometimes seem to. Might be fixed if we
						// move to a safer backend.
						logger_.warning("Inconsistent vCard photo hash cache");
						hash = newHash;
					}
					avatarStorage_.addAvatar(hash, photo);
				}
				else {
					// Can happen if the cache is inconsistent.
					hash = "";
				}
			}
		}
		return hash;
	}

	private void handleVCardChanged(JID from) {
		// We don't check whether the avatar actually changed. Direct use of this
		// manager could cause unnecessary updates, but in practice, this will be
		// caught by the wrapping CombinedAvatarManager anyway.
		onAvatarChanged.emit(from);
	}

	private JID getAvatarJID(JID jid) {
		JID bareFrom = jid.toBare();
		return (mucRegistry_ != null && mucRegistry_.isMUC(bareFrom)) ? jid : bareFrom;
	}
}
