/*
 * Copyright (c) 2012-2015 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
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

import com.isode.stroke.base.SafeByteArray;

public class HTTPConnectProxiedConnectionFactory implements ConnectionFactory {

	private DomainNameResolver resolver_;
	private ConnectionFactory connectionFactory_;
	private TimerFactory timerFactory_;
	private String proxyHost_;
	private int proxyPort_;
	private SafeByteArray authID_;
	private SafeByteArray authPassword_;
	private HTTPTrafficFilter httpTrafficFilter_;

	public HTTPConnectProxiedConnectionFactory(DomainNameResolver resolver, ConnectionFactory connectionFactory, TimerFactory timerFactory, final String proxyHost, int proxyPort) {
		this(resolver, connectionFactory, timerFactory, proxyHost, proxyPort, null);
	}

	public HTTPConnectProxiedConnectionFactory(DomainNameResolver resolver, ConnectionFactory connectionFactory, TimerFactory timerFactory, final String proxyHost, int proxyPort, HTTPTrafficFilter httpTrafficFilter) {
		resolver_ = resolver;
		connectionFactory_ = connectionFactory;
		timerFactory_ = timerFactory;
		proxyHost_ = proxyHost;
		proxyPort_ = proxyPort;
		authID_ = new SafeByteArray("");
		authPassword_ = new SafeByteArray("");
		httpTrafficFilter_ = httpTrafficFilter;
	}

	public HTTPConnectProxiedConnectionFactory(DomainNameResolver resolver, ConnectionFactory connectionFactory, TimerFactory timerFactory, final String proxyHost, int proxyPort, final SafeByteArray authID, final SafeByteArray authPassword) {
		this(resolver, connectionFactory, timerFactory, proxyHost, proxyPort, authID, authPassword, null);
	}

	public HTTPConnectProxiedConnectionFactory(DomainNameResolver resolver, ConnectionFactory connectionFactory, TimerFactory timerFactory, final String proxyHost, int proxyPort, final SafeByteArray authID, final SafeByteArray authPassword, HTTPTrafficFilter httpTrafficFilter) {
		resolver_ = resolver;
		connectionFactory_ = connectionFactory;
		timerFactory_ = timerFactory;
		proxyHost_ = proxyHost;
		proxyPort_ = proxyPort;
		authID_ = authID;
		authPassword_ = authPassword;
		httpTrafficFilter_ = httpTrafficFilter;
	}

	public Connection createConnection() {
		HTTPConnectProxiedConnection proxyConnection = HTTPConnectProxiedConnection.create(resolver_, connectionFactory_, timerFactory_, proxyHost_, proxyPort_, authID_, authPassword_);
		proxyConnection.setHTTPTrafficFilter(httpTrafficFilter_);
		return proxyConnection;
	}
}