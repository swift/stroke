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

import com.isode.stroke.avatars.AvatarManager;
import com.isode.stroke.base.ByteArray;
import com.isode.stroke.jid.JID;
import java.nio.file.*;
import java.io.File;
import java.util.*;

public class DummyAvatarManager implements AvatarManager {

	private Map<JID, ByteArray> avatars = new HashMap<JID, ByteArray>();

	public Path getAvatarPath(JID j) {
		return (Paths.get("/avatars" + File.separator + j.toString())).toAbsolutePath();
	}

	public ByteArray getAvatar(JID jid) {
		if(avatars.containsKey(jid)) {
			return avatars.get(jid);
		} else {
			return new ByteArray();
		}
	}
}