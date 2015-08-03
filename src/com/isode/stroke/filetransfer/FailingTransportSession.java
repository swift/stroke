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

public class FailingTransportSession extends TransportSession {

	public void start() {
		assert(false);
		onFinished.emit(new FileTransferError(FileTransferError.Type.PeerError));
	}

	public void stop() {
	}
}