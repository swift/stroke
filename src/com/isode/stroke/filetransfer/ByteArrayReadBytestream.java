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

package com.isode.stroke.filetransfer;

import com.isode.stroke.base.ByteArray;

public class ByteArrayReadBytestream extends ReadBytestream {

	private ByteArray data;
	private int position;
	private boolean dataComplete;

	public ByteArrayReadBytestream(final ByteArray data) {
		this.data = data;
		this.position = 0;
		this.dataComplete = true;
	}

	public ByteArray read(int size) {
		int readSize = size;
		if (position + readSize > data.getSize()) {
			readSize = data.getSize() - position;
		}
		String s = new String(data.getData());
		s = s.substring(position, position+readSize);
		ByteArray result = new ByteArray(s);

		onRead.emit(result);
		position += readSize;
		return result;
	}

	public boolean isFinished() {
		return position >= data.getSize() && dataComplete;
	}

	public void setDataComplete(boolean b) {
		dataComplete = b;
	}

	public void addData(final ByteArray moreData) {
		data.append(moreData);
		onDataAvailable.emit();
	}
}