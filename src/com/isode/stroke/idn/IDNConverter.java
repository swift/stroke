/*
 * Copyright (c) 2013 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.idn;

import com.isode.stroke.base.SafeByteArray;

public interface IDNConverter {

	public enum StringPrepProfile {
		NamePrep,
		XMPPNodePrep,
		XMPPResourcePrep,
		SASLPrep
	}

	public String getStringPrepared(String s, StringPrepProfile profile) throws IllegalArgumentException;
	public SafeByteArray getStringPrepared(SafeByteArray s, StringPrepProfile profile) throws IllegalArgumentException;

	// Thread-safe
	public String getIDNAEncoded(String s);
}