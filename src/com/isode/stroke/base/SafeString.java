/*
 * Copyright (c) 2011-2015 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.base;

import com.isode.stroke.base.SafeByteArray;

public class SafeString {

	private SafeByteArray data;

	public SafeString(SafeByteArray data) {
		this.data = data;
	}

	public SafeString(String s) {
		this.data = new SafeByteArray(s);
	}

	public SafeByteArray getData() {
		return data;
	}
}