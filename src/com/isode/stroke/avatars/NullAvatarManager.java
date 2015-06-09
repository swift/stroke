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

public class NullAvatarManager implements AvatarManager {

	public Path getAvatarPath(JID j) {
		return Paths.get("");
	}

	public ByteArray getAvatar(JID jid) {
		return new ByteArray();
	}
}