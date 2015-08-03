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

package com.isode.stroke.filetransfer;

import com.isode.stroke.signals.SignalConnection;

public class IBBReceiveTransportSession extends TransportSession {

	private IBBReceiveSession session;
	private SignalConnection finishedConnection;
	private SignalConnection bytesSentConnection;

	public IBBReceiveTransportSession(IBBReceiveSession session) {
		this.session = session;
		finishedConnection = session.onFinished.connect(onFinished);
	}

	public void start() {
		session.start();
	}

	public void stop() {
		session.stop();
	}
}