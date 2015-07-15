/*
 * Copyright (c) 2010-2013 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.component;

import com.isode.stroke.component.CoreComponent;
import com.isode.stroke.queries.responders.SoftwareVersionResponder;
import com.isode.stroke.jid.JID;
import com.isode.stroke.network.NetworkFactories;

/**
 * Provides the core functionality for writing XMPP component software.
 *
 * Besides connecting to an XMPP server, this class also provides interfaces for
 * performing most component tasks on the XMPP network.
 */
public class Component extends CoreComponent {

	private SoftwareVersionResponder softwareVersionResponder;

	public Component(JID jid, String secret, NetworkFactories networkFactories) {
		super(jid, secret, networkFactories);
		softwareVersionResponder = new SoftwareVersionResponder(getIQRouter());
		softwareVersionResponder.start();
	}

	public void delete() {
		softwareVersionResponder.stop();
	}

	/**
	 * Sets the software version of the client.
	 *
	 * This will be used to respond to version queries from other entities.
	 */
	public void setSoftwareVersion(final String name, final String version) {
		softwareVersionResponder.setVersion(name, version);
	}	
}