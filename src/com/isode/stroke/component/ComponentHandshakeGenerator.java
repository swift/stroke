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

package com.isode.stroke.component;

import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.crypto.JavaCryptoProvider;
import com.isode.stroke.stringcodecs.Hexify;
import com.isode.stroke.base.ByteArray;

public class ComponentHandshakeGenerator {

	public static String getHandshake(String streamID, String secret, CryptoProvider crypto) {
		String concatenatedString = streamID + secret;
		concatenatedString = concatenatedString.replaceAll("&", "&amp;");
		concatenatedString = concatenatedString.replaceAll("<", "&lt;");
		concatenatedString = concatenatedString.replaceAll(">", "&gt;");
		concatenatedString = concatenatedString.replaceAll("\'", "&apos;");
		concatenatedString = concatenatedString.replaceAll("\"", "&quot;");
		return Hexify.hexify(crypto.getSHA1Hash(new ByteArray(concatenatedString)));
	}
}