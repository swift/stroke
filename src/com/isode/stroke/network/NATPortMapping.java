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

public class NATPortMapping {

	private int publicPort;
	private int localPort;
	private Protocol protocol;
	private int leaseInSeconds;

	public enum Protocol {
		TCP,
		UDP
	};

	public NATPortMapping(int localPort, int publicPort) {
		this(localPort, publicPort, Protocol.TCP, 60 * 60 * 24);
	}

	public NATPortMapping(int localPort, int publicPort, Protocol protocol) {
		this(localPort, publicPort, protocol, 60 * 60 * 24);
	}

	public NATPortMapping(int localPort, int publicPort, Protocol protocol, int leaseInSeconds) {
		this.localPort = localPort;
		this.publicPort = publicPort;
		this.protocol = protocol;
		this.leaseInSeconds = leaseInSeconds;
	}

	public int getPublicPort() {
		return publicPort;
	}

	public int getLocalPort() {
		return localPort;
	}

	public Protocol getProtocol() {
		return protocol;
	}

	public int getLeaseInSeconds() {
		return leaseInSeconds;
	}
}