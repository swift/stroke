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

import com.isode.stroke.signals.Signal1;
import com.isode.stroke.signals.SignalConnection;

public class S5BTransportSession<T> extends TransportSession {

	public S5BTransportSession(
		T session,
		ReadBytestream readStream) {
			this.session = session;
			this.readStream = readStream;
			initialize();
	}

	public S5BTransportSession(
		T session,
		WriteBytestream writeStream) {
			this.session = session;
			this.writeStream = writeStream;
			initialize();
	}

	public void start() {
		if (readStream != null) {
			//session.startSending(readStream);
		}
		else {
			//session.startReceiving(writeStream);
		}
	}

	public void stop() {
		//session.stop();
	}

	private void initialize() {
		//finishedConnection = session.onFinished.connect(onFinished);
		//bytesSentConnection = session.onBytesSent.connect(onBytesSent);
	}

	private T session;
	private ReadBytestream readStream;
	private WriteBytestream writeStream;

	private SignalConnection finishedConnection;
	private SignalConnection bytesSentConnection;
}
