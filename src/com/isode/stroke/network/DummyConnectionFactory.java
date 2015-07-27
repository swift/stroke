/*
 * Copyright (c) 2011 Tobias Markmann
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.network;

import com.isode.stroke.eventloop.EventLoop;

public class DummyConnectionFactory implements ConnectionFactory {

	private EventLoop eventLoop;

	public DummyConnectionFactory(EventLoop eventLoop) {
		this.eventLoop = eventLoop;
	}

	public Connection createConnection() {
		return new DummyConnection(eventLoop);
	}
}