/*
 * Copyright (c) 2010-2015 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.network;

import com.isode.stroke.eventloop.EventOwner;
import com.isode.stroke.eventloop.EventLoop;

public class DummyConnectionServer extends ConnectionServer implements EventOwner {

	private HostAddressPort localAddressPort;

	public DummyConnectionServer(EventLoop eventLoop, int port) {
		this.localAddressPort = new HostAddressPort(new HostAddress(), port);
	}

	public DummyConnectionServer(EventLoop eventLoop, final HostAddress hostAddress, int port) {
		this.localAddressPort = new HostAddressPort(hostAddress, port);
	}

	public HostAddressPort getAddressPort() {
		return localAddressPort;
	}

	public Error tryStart() {
		return null;
	}

	public void start() {

	}

	public void stop() {

	}
}