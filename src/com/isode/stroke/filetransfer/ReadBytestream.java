/*
 * Copyright (c) 2010 Isode Limited.
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
import com.isode.stroke.signals.Signal;
import com.isode.stroke.base.ByteArray;

public abstract class ReadBytestream {

	public final Signal onDataAvailable = new Signal();
	public final Signal1<ByteArray> onRead = new Signal1<ByteArray>();

	/**
	 * Return an empty vector if no more data is available.
	 * Use onDataAvailable signal for signaling there is data available again.
	 */
	public abstract ByteArray read(int size);

	public abstract boolean isFinished();

}