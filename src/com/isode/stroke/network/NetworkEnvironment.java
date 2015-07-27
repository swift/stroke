/*
 * Copyright (c) 2011 Tobias Markmann
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
/*
 * Copyright (c) 2011 Isode Limited.
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

public abstract class NetworkEnvironment {

	public abstract Vector<NetworkInterface> getNetworkInterfaces();

	public HostAddress getLocalAddress() {
		Vector<NetworkInterface> networkInterfaces = getNetworkInterfaces();
		for (final NetworkInterface iface : networkInterfaces) {
			if (!iface.isLoopback()) {
				for (final HostAddress address : iface.getAddresses()) {
					if (address.getInetAddress() != null) {
						return address;
					}
				}
			}
		}
		return new HostAddress();
	}
}