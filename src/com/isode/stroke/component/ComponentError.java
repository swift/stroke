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

package com.isode.stroke.component;

public class ComponentError {

	public enum Type {
		UnknownError,
		ConnectionError,
		ConnectionReadError,
		ConnectionWriteError,
		XMLError,
		AuthenticationFailedError,
		UnexpectedElementError
	};

	private Type type_;

	public ComponentError() {
		this(Type.UnknownError);
	}

	public ComponentError(Type type) {
		this.type_ = type;
	}

	public Type getType() {
		return type_;
	}
}