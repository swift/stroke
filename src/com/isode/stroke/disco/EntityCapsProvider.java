/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.disco;

import com.isode.stroke.elements.DiscoInfo;
import com.isode.stroke.jid.JID;
import com.isode.stroke.signals.Signal1;

public abstract class EntityCapsProvider {
	/**
	 * Returns the service discovery information of the given JID.
	 */
	public abstract DiscoInfo getCaps(final JID jid);

	/**
	 * Emitted when the capabilities of a JID changes.
	 */
	public final Signal1<JID> onCapsChanged = new Signal1<JID>();
}
