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

package com.isode.stroke.sasl;

import com.isode.stroke.sasl.ClientAuthenticator;
import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.base.ByteArray;

public class EXTERNALClientAuthenticator extends ClientAuthenticator {

	private boolean finished;

	public EXTERNALClientAuthenticator() {
		super("EXTERNAL");
		this.finished = false;
	}

	public SafeByteArray getResponse() {
		return null;
	}

	public boolean setChallenge(final ByteArray byteArray) {
		if (finished) {
			return false;
		}
		finished = true;
		return true;
	}
}