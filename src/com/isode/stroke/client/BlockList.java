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

package com.isode.stroke.client;

import com.isode.stroke.signals.Signal1;
import com.isode.stroke.signals.Signal;
import com.isode.stroke.jid.JID;
import java.util.Vector;

public abstract class BlockList {

	public enum State {
		Init,
		Requesting,
		Available,
		Error
	};

	public final Signal onStateChanged = new Signal();
	public final Signal1<JID> onItemAdded = new Signal1<JID>();
	public final Signal1<JID> onItemRemoved = new Signal1<JID>();

	public abstract State getState();

	public abstract Vector<JID> getItems();

	public boolean isBlocked(final JID jid) {
		final Vector<JID> items = getItems();
		return (items.contains(jid.toBare()) || items.contains(new JID(jid.getDomain())) || items.contains(jid));
	}
}