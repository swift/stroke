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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import com.isode.stroke.base.ByteArray;

public class FileReadBytestream extends ReadBytestream {

	private String file;
	private FileInputStream stream;

	public FileReadBytestream(final String file) {
		this.file = file;
		this.stream = null;
	}

	public ByteArray read(int size) {
		try {
			if (stream == null) {
				stream = new FileInputStream(file);
			}
			//assert(stream.good());
			byte[] buffer = new byte[size];
			stream.read(buffer, 0, size);
			ByteArray result = new ByteArray(buffer);
			onRead.emit(result);
			return result;
		}
		catch (FileNotFoundException e) {
			return null;
		}
		catch (IOException e) {
			return null;
		} finally {
			try {
				if(stream != null) stream.close();
			}
			catch (IOException e) {
				// Needs a catch clause
			}
		}
	}

	public boolean isFinished() {
		return stream != null;
	}
}