/*
 * Copyright (c) 2013 Isode Limited.
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

public abstract class TransportSession {

	public abstract void start();
	public abstract void stop();

	public final Signal1<Integer> onBytesSent = new Signal1<Integer>();
	public final Signal1<FileTransferError> onFinished = new Signal1<FileTransferError>();
}
