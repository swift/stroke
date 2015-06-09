/*
 * Copyright (c) 2010-2013 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.avatars;

import com.isode.stroke.avatars.AvatarManager;
import com.isode.stroke.avatars.CombinedAvatarProvider;
import com.isode.stroke.muc.MUCRegistry;
import com.isode.stroke.avatars.AvatarStorage;
import com.isode.stroke.client.StanzaChannel;
import com.isode.stroke.vcards.VCardManager;
import com.isode.stroke.avatars.VCardUpdateAvatarManager;
import com.isode.stroke.avatars.VCardAvatarManager;
import com.isode.stroke.avatars.OfflineAvatarManager;
import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.base.ByteArray;
import com.isode.stroke.jid.JID;
import com.isode.stroke.signals.Slot1;
import java.nio.file.*;
import com.isode.stroke.signals.SignalConnection;

public class AvatarManagerImpl implements AvatarManager {

	private CombinedAvatarProvider combinedAvatarProvider = new CombinedAvatarProvider();
	private AvatarStorage avatarStorage;
	private VCardUpdateAvatarManager vcardUpdateAvatarManager;
	private VCardAvatarManager vcardAvatarManager;
	private OfflineAvatarManager offlineAvatarManager;
	private SignalConnection onAvatarChangedConnection;

	public AvatarManagerImpl(VCardManager vcardManager, StanzaChannel stanzaChannel, AvatarStorage avatarStorage, CryptoProvider crypto) {
		this(vcardManager, stanzaChannel, avatarStorage, crypto, null);
	}

	public AvatarManagerImpl(VCardManager vcardManager, StanzaChannel stanzaChannel, AvatarStorage avatarStorage, CryptoProvider crypto, MUCRegistry mucRegistry) {
		this.avatarStorage = avatarStorage;
		vcardUpdateAvatarManager = new VCardUpdateAvatarManager(vcardManager, stanzaChannel, avatarStorage, crypto, mucRegistry);
		combinedAvatarProvider.addProvider(vcardUpdateAvatarManager);

		vcardAvatarManager = new VCardAvatarManager(vcardManager, avatarStorage, crypto, mucRegistry);
		combinedAvatarProvider.addProvider(vcardAvatarManager);

		offlineAvatarManager = new OfflineAvatarManager(avatarStorage);
		combinedAvatarProvider.addProvider(offlineAvatarManager);

		onAvatarChangedConnection = combinedAvatarProvider.onAvatarChanged.connect(new Slot1<JID>() {

			public void call(JID p1) {
				handleCombinedAvatarChanged(p1);
			}
		});
	}

	public Path getAvatarPath(JID jid) {
		String hash = combinedAvatarProvider.getAvatarHash(jid);
		if (hash != null && hash.length() != 0) {
			return avatarStorage.getAvatarPath(hash);
		}
		return Paths.get("");
	}

	public ByteArray getAvatar(JID jid) {
		String hash = combinedAvatarProvider.getAvatarHash(jid);
		if (hash != null && hash.length() != 0) {
			return avatarStorage.getAvatar(hash);
		}
		return new ByteArray();
	}

	private void handleCombinedAvatarChanged(JID jid) {
		String hash = combinedAvatarProvider.getAvatarHash(jid);
		assert(hash != null);
		offlineAvatarManager.setAvatar(jid, hash);
		onAvatarChanged.emit(jid);
	}
}