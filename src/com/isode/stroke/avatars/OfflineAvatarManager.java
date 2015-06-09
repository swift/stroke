/*
 * Copyright (c) 2010 Isode Limited.
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
import com.isode.stroke.avatars.AvatarStorage;
import com.isode.stroke.jid.JID;

public class OfflineAvatarManager implements AvatarProvider {

	private AvatarStorage avatarStorage;

	public OfflineAvatarManager(AvatarStorage avatarStorage) {
		this.avatarStorage = avatarStorage;
	}

	public String getAvatarHash(JID jid) {
		return avatarStorage.getAvatarForJID(jid);
	}

	public void setAvatar(JID jid, String hash) {
		if (!getAvatarHash(jid).equals(hash)) {
			avatarStorage.setAvatarForJID(jid, hash);
			onAvatarChanged.emit(jid);
		}
	}
}