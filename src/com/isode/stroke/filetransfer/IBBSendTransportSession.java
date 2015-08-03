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

public class IBBSendTransportSession extends TransportSession {

	private IBBSendSession session;
	private SignalConnection finishedConnection;
	private SignalConnection bytesSentConnection;

	public IBBSendTransportSession(IBBSendSession session) {
		this.session = session;
		finishedConnection = session.onFinished.connect(onFinished);
		bytesSentConnection = session.onBytesSent.connect(onBytesSent);
	}

	public void start() {
		session.start();
	}

	public void stop() {
		session.stop();
	}
}