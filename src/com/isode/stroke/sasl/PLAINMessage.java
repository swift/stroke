/*
 * Copyright (c) 2010-2013 Isode Limited.
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

public class PLAINMessage {

	private String authcid = "";
	private String authzid = "";
	private SafeByteArray password = new SafeByteArray();

	public PLAINMessage(final String authcid, final SafeByteArray password) {
		this(authcid, password, "");
	}

	public PLAINMessage(final String authcid, final SafeByteArray password, final String authzid) {
		this.authcid = authcid;
		this.password = password;
		this.authzid = authzid;
	}

	public PLAINMessage(final SafeByteArray value) {
		int i = 0;
		byte[] byteArrayValue = value.getData();
		while (i < value.getSize() && byteArrayValue[i] != ((byte)0)) {
			authzid += (char)(byteArrayValue[i]);
			++i;
		}
		if (i == value.getSize()) {
			return;
		}
		++i;
		while (i < value.getSize() && byteArrayValue[i] != ((byte)0)) {
			authcid += (char)(byteArrayValue[i]);
			++i;
		}
		if (i == value.getSize()) {
			authcid = "";
			return;
		}
		++i;
		while (i < value.getSize()) {
			password.append(byteArrayValue[i]);
			++i;
		}
	}

	public SafeByteArray getValue() {
		return new SafeByteArray().append(authzid).append((byte)0).append(authcid).append((byte)0).append(password);
	}

	public String getAuthenticationID() {
		return authcid;
	}

	public SafeByteArray getPassword() {
		return password;
	}

	public String getAuthorizationID() {
		return authzid;
	}	
}