/*
 * Copyright (c) 2010-2011 Thilo Cestonaro
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.network;

public class JavaProxyProvider implements ProxyProvider {

	private EnvironmentProxyProvider environmentProxyProvider;

	public JavaProxyProvider() {

	}

	public HostAddressPort getHTTPConnectProxy() {
		HostAddressPort proxy;
		proxy = environmentProxyProvider.getHTTPConnectProxy();
		if(proxy.isValid()) {
			return proxy;
		}
		return new HostAddressPort(new HostAddress(), 0);
	}

	public HostAddressPort getSOCKS5Proxy() {
		HostAddressPort proxy;
		proxy = environmentProxyProvider.getSOCKS5Proxy();
		if(proxy.isValid()) {
			return proxy;
		}
		return new HostAddressPort(new HostAddress(), 0);
	}
}