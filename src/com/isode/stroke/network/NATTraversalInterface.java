/*
 * Copyright (c) 2011-2015 Isode Limited.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.network;

public interface NATTraversalInterface {

	public boolean isAvailable();

	public HostAddress getPublicIP();
	public NATPortMapping addPortForward(int localPort, int publicPort);
	public boolean removePortForward(final NATPortMapping map);
}