/*
 * Copyright (c) 2011 Tobias Markmann
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
/*
 * Copyright (c) 2015 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.network;

import java.util.Vector;

public class NetworkInterface {

	private String name = "";
	private boolean loopback;
	private Vector<HostAddress> addresses = new Vector<HostAddress>();

	public NetworkInterface(final String name, boolean loopback) {
		this.name = name;
		this.loopback = loopback;
	}

	public void addAddress(final HostAddress address) {
		addresses.add(address);
	}

	public Vector<HostAddress> getAddresses() {
		return addresses;
	}

	public String getName() {
		return name;
	}

	public boolean isLoopback() {
		return loopback;
	}
}