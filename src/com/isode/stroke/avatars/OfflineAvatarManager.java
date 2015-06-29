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
import com.isode.stroke.avatars.AvatarStorage;
import com.isode.stroke.jid.JID;

public class OfflineAvatarManager extends AvatarProvider {

	private AvatarStorage avatarStorage;

	public OfflineAvatarManager(AvatarStorage avatarStorage) {
		this.avatarStorage = avatarStorage;
	}

	@Override
	public void delete() {
	}

	@Override
	public String getAvatarHash(JID jid) {
		return avatarStorage.getAvatarForJID(jid);
	}

	public void setAvatar(JID jid, String hash) {
		if (!hash.equals(getAvatarHash(jid))) {
			avatarStorage.setAvatarForJID(jid, hash);
			onAvatarChanged.emit(jid);
		}
	}
}