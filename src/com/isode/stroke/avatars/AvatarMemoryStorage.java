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

import com.isode.stroke.avatars.AvatarStorage;
import com.isode.stroke.base.ByteArray;
import com.isode.stroke.jid.JID;

import java.util.*;

public class AvatarMemoryStorage implements AvatarStorage {

	private Map<String, ByteArray> avatars = new HashMap<String, ByteArray>();
	private Map<JID, String> jidAvatars = new HashMap<JID, String>();

	@Override
	public boolean hasAvatar(String hash) {
		return avatars.containsKey(hash);
	}

	@Override
	public void addAvatar(String hash, ByteArray avatar) {
		avatars.put(hash, avatar);
	}

	@Override
	public String getAvatar(String hash) {
		return avatars.containsKey(hash) ? hash : null;
	}

	@Override
	public void setAvatarForJID(JID jid, String hash) {
		jidAvatars.put(jid, hash);
	}

	@Override
	public String getAvatarForJID(JID jid) {
		if(jidAvatars.containsKey(jid)) {
			return jidAvatars.get(jid);
		} else {
			return null;
		}
	}
	
	// Used for test cases only
	public ByteArray getAvatarBytes(String hash) {
		return avatars.get(hash);
	}

}