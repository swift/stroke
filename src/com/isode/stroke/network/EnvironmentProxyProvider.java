/*
 * Copyright (c) 2010-2011 Thilo Cestonaro
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

import java.util.logging.Logger;

public class EnvironmentProxyProvider implements ProxyProvider {

	public EnvironmentProxyProvider() {
		socksProxy = getFromEnv("all_proxy", "socks");
		httpProxy = getFromEnv("http_proxy", "http");
		logger_.fine("Environment: SOCKS5 => " + socksProxy.toString() + "; HTTP Connect => " + httpProxy.toString() + "\n");
	}

	public HostAddressPort getHTTPConnectProxy() {
		return httpProxy;
	}

	public HostAddressPort getSOCKS5Proxy() {
		return socksProxy;
	}

	private HostAddressPort getFromEnv(final String envVarName, String proxyProtocol) {
		String envVar = null;
		String address = "";
		int port = 0;

		envVar = System.getenv(envVarName);

		proxyProtocol += "://";
		address = envVar != null ? envVar : "0.0.0.0";
		if(envVar != null && address.substring(0, proxyProtocol.length()).equals(proxyProtocol)) {
			address = address.substring(proxyProtocol.length(), address.length());
			port = Integer.parseInt(address.substring(address.indexOf(':') + 1, address.length()));
			address = address.substring(0, address.indexOf(':'));
		}

		return new HostAddressPort(new HostAddress(address), port);
	}

	private HostAddressPort socksProxy;
	private HostAddressPort httpProxy;
	private final Logger logger_ = Logger.getLogger(this.getClass().getName());
}