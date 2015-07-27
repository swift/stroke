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

public class SOCKS5ProxiedConnectionFactory implements ConnectionFactory {

	private DomainNameResolver resolver_;
	private ConnectionFactory connectionFactory_;
	private TimerFactory timerFactory_;
	private String proxyHost_ = "";
	private int proxyPort_;

	public SOCKS5ProxiedConnectionFactory(DomainNameResolver resolver, ConnectionFactory connectionFactory, TimerFactory timerFactory, final String proxyHost, int proxyPort) {
		resolver_ = resolver;
		connectionFactory_ = connectionFactory;
		timerFactory_ = timerFactory;
		proxyHost_ = proxyHost;
		proxyPort_ = proxyPort;
	}

	public Connection createConnection() {
		return SOCKS5ProxiedConnection.create(resolver_, connectionFactory_, timerFactory_, proxyHost_, proxyPort_);
	}
}