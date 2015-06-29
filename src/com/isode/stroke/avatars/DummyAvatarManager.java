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

import com.isode.stroke.jid.JID;

public class DummyAvatarManager extends AvatarManager {

	@Override
	public String getAvatar(final JID j) {
		return null;
	}

}