/*
 * Copyright (c) 2011-2015 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.crypto;

import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.crypto.Hash;
import com.isode.stroke.base.ByteArray;
import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.base.NotNull;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.InvalidKeyException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.lang.IllegalStateException;

public class JavaCryptoProvider extends CryptoProvider {

	private static class HashProvider implements Hash {

		private final MessageDigest digest;

		/**
		* Constructor, MessageDigest object that implements MD5 / SHA1.
		*/
		public HashProvider(String algorithm) {
			try {
				digest = MessageDigest.getInstance(algorithm);
			}
			catch (NoSuchAlgorithmException e) {
				throw new RuntimeException(e.getMessage());
			}
		}

		/**
		* Updates the digest using the ByteArray.
		* @param data, NotNull.
		* @return Hash updated with data.
		*/
		@Override
		public Hash update(ByteArray data) {
			NotNull.exceptIfNull(data, "data");
			digest.update(data.getData());
			return this;
		}

		/**
		* Updates the digest using the SafeByteArray.
		* @param data, NotNull.
		* @return Hash updated with data.
		*/
		@Override
		public Hash update(SafeByteArray data) {
			NotNull.exceptIfNull(data, "data");
			digest.update(data.getData());
			return this;
		}

		/**
		* Completes the MD5/SHA1 hash computation.
		* @return ByteArray containing the MD5/SHA1 Hash.
		*/
		@Override
		public ByteArray getHash() {
			return new ByteArray(digest.digest());
		}

	}

	/**
	* Computes the HMACSHA1 hash computation.
	* @param key NotNull. Key is used for initializing MAC object.
	* @param data NotNull.
	* @return ByteArray containing the HMACSHA1 Hash.
	*/
	public ByteArray getHMACSHA1Internal(final ByteArray key, final ByteArray data) {
		NotNull.exceptIfNull(key, "key");
		NotNull.exceptIfNull(data, "data");
		try {
			SecretKeySpec signingKey = new SecretKeySpec(key.getData(), "HmacSHA1");
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(signingKey);
			mac.update(data.getData());
			byte[] Hmac = mac.doFinal();
			return (new ByteArray(Hmac));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e.getMessage());
		} catch (InvalidKeyException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	* Creates the SHA1Hash object for performing hash computations.
	* @return SHA1Hash object.
	*/
	@Override
	public Hash createSHA1() {
		return new HashProvider("SHA-1");
	}

	/**
	* Creates the SHA1Hash object for performing hash computations.
	* @return MD5Hash object.
	*/
	@Override
	public Hash createMD5() {
		return new HashProvider("MD5");
	}

	/**
	* @param key, NotNull.
	* @param data, NotNull.
	* @return ByteArray containing the HMACSHA1 Hash.
	*/
	@Override
	public ByteArray getHMACSHA1(final SafeByteArray key, final ByteArray data) {
		return getHMACSHA1Internal(key, data);
	}

	/**
	* @param key, NotNull.
	* @param data, NotNull.
	* @return ByteArray containing the HMACSHA1 Hash.
	*/
	@Override
	public ByteArray getHMACSHA1(final ByteArray key, final ByteArray data) {
		return getHMACSHA1Internal(key, data);
	}

	@Override
	public boolean isMD5AllowedForCrypto() {
		return true;
	}
}