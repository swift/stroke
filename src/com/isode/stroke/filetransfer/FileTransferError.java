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

public class FileTransferError {

	private Type type;

	public enum Type {
		UnknownError,
		PeerError,
		ReadError,
		WriteError,
		ClosedError
	};

	public FileTransferError() {
		this.type = Type.UnknownError;
	}

	public FileTransferError(Type type) {
		this.type = type;
	}

	public Type getType() {
		return type;
	}
}
