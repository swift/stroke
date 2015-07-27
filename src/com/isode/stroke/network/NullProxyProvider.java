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

public class NullProxyProvider implements ProxyProvider {

	public HostAddressPort getHTTPConnectProxy() {
		return new HostAddressPort();
	}

	public HostAddressPort getSOCKS5Proxy() {
		return new HostAddressPort();
	}
}