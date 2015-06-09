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

import com.isode.stroke.avatars.AvatarStorage;
import com.isode.stroke.base.ByteArray;
import com.isode.stroke.jid.JID;
import java.nio.file.*;
import java.io.File;
import java.util.*;

public class AvatarMemoryStorage implements AvatarStorage {

	private Map<String, ByteArray> avatars = new HashMap<String, ByteArray>();
	private Map<JID, String> jidAvatars = new HashMap<JID, String>();

	public boolean hasAvatar(String hash) {
		return avatars.containsKey(hash);
	}

	public void addAvatar(String hash, ByteArray avatar) {
		avatars.put(hash, avatar);
	}

	public ByteArray getAvatar(String hash) {
		if(avatars.containsKey(hash)) {
			return avatars.get(hash);
		} else {
			return new ByteArray();
		}
	}

	public Path getAvatarPath(String hash) {
		return (Paths.get("/avatars" + File.separator + hash)).toAbsolutePath();
	}

	public void setAvatarForJID(JID jid, String hash) {
		jidAvatars.put(jid, hash);
	}

	public String getAvatarForJID(JID jid) {
		if(jidAvatars.containsKey(jid)) {
			return jidAvatars.get(jid);
		} else {
			return "";
		}
	}
}