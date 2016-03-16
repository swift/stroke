/*
 * Copyright (c) 2010-2016 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.filetransfer;

import com.isode.stroke.base.ByteArray;

public class ByteArrayWriteBytestream extends WriteBytestream {

	private ByteArray data = new ByteArray();

	public ByteArrayWriteBytestream() {
	}

	@Override
    public boolean write(final ByteArray bytes) {
		data.append(bytes);
		onWrite.emit(bytes);
		return true;
	}

	public ByteArray getData() {
		return data;
	}
}