/*
 * Copyright (c) 2012-2015 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.tls;

import com.isode.stroke.base.Error;

public class TLSError implements Error {

	private Type type;

	public enum Type {
		UnknownError,
		CertificateCardRemoved
	};

	public TLSError() {
		this(Type.UnknownError);
	}

	public TLSError(Type type) {
		this.type = type;
	}

	public Type getType() { 
		return type; 
	}
}