/*
 * Copyright (c) 2014 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.network;

import com.isode.stroke.eventloop.EventLoop;

public class DummyConnectionServerFactory implements ConnectionServerFactory {

	private EventLoop eventLoop;

	public DummyConnectionServerFactory(EventLoop eventLoop) {
		this.eventLoop = eventLoop;
	}

	public ConnectionServer createConnectionServer(int port) {
		return new DummyConnectionServer(eventLoop, port);
	}

	public ConnectionServer createConnectionServer(final HostAddress hostAddress, int port) {
		return new DummyConnectionServer(eventLoop, hostAddress, port);
	}
}