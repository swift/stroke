/*
 * Copyright (c) 2011 Isode Limited.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.network;

public class NullNATTraversalInterface implements NATTraversalInterface {

	public boolean isAvailable() {
		return true;
	}

	public HostAddress getPublicIP() {
		return null;
	}

	public NATPortMapping addPortForward(int localPort, int publicPort) {
		return null;
	}

	public boolean removePortForward(final NATPortMapping map) {
		return false;
	}
}