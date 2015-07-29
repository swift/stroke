/*
 * Copyright (c) 2011-2015 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.jingle;

import com.isode.stroke.jingle.JingleSession;
import com.isode.stroke.elements.JingleContentPayload;
import com.isode.stroke.jid.JID;
import java.util.Vector;

public interface IncomingJingleSessionHandler {

	public boolean handleIncomingJingleSession(JingleSession session, final Vector<JingleContentPayload> contents, final JID recipient);
}