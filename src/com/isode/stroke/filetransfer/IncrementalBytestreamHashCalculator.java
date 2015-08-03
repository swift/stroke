/*
 * Copyright (c) 2011 Tobias Markmann
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
/*
 * Copyright (c) 2013-2014 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.filetransfer;

import com.isode.stroke.crypto.Hash;
import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.base.ByteArray;
import com.isode.stroke.stringcodecs.Hexify;

public class IncrementalBytestreamHashCalculator {

	private Hash md5Hasher;
	private Hash sha1Hasher;
	private ByteArray md5Hash;
	private ByteArray sha1Hash;

	public IncrementalBytestreamHashCalculator(boolean doMD5, boolean doSHA1, CryptoProvider crypto) {
		md5Hasher = doMD5 ? crypto.createMD5() : null;
		sha1Hasher = doSHA1 ? crypto.createSHA1() : null;
	}

	public void feedData(final ByteArray data) {
		if (md5Hasher != null) {
			md5Hasher.update(data);
		}
		if (sha1Hasher != null) {
			sha1Hasher.update(data);
		}
	}

	/*void feedData(const SafeByteArray& data) {
		if (md5Hasher) {
			md5Hasher.update(createByteArray(data.data(), data.size()));
		}
		if (sha1Hasher) {
			sha1Hasher.update(createByteArray(data.data(), data.size()));
		}
	}*/

	public ByteArray getSHA1Hash() {
		assert(sha1Hasher != null);
		if (sha1Hash == null) {
			sha1Hash = sha1Hasher.getHash();
		}
		return sha1Hash;
	}

	public ByteArray getMD5Hash() {
		assert(md5Hasher != null);
		if (md5Hash == null) {
			md5Hash = md5Hasher.getHash();
		}
		return md5Hash;
	}

	public String getSHA1String() {
		assert(sha1Hasher != null);
		return Hexify.hexify(getSHA1Hash());
	}

	public String getMD5String() {
		assert(md5Hasher != null);
		return Hexify.hexify(getMD5Hash());	
	}
}