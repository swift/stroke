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
import com.isode.stroke.signals.Signal1;

public abstract class AvatarManager {

	public abstract String getAvatar(JID jid);
	public Signal1<JID> onAvatarChanged = new Signal1<JID>();
}