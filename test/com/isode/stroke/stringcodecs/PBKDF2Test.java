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

package com.isode.stroke.stringcodecs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Before;
import com.isode.stroke.base.ByteArray;
import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.stringcodecs.PBKDF2;
import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.crypto.JavaCryptoProvider;

public class PBKDF2Test {

	private CryptoProvider crypto;

	@Before
	public void setUp() {
		crypto = new JavaCryptoProvider();
	}

	@Test
	public void testGetResult_I1() {
		ByteArray result = PBKDF2.encode(new SafeByteArray("password"), new ByteArray("salt"), 1, crypto);

		assertEquals(new ByteArray("0c60c80f961f0e71f3a9b524af6012062fe037a6"), new ByteArray(Hexify.hexify(result)));
	}

	@Test
	public void testGetResult_I2() {
		ByteArray result = PBKDF2.encode(new SafeByteArray("password"), new ByteArray("salt"), 2, crypto);

		assertEquals(new ByteArray("ea6c014dc72d6f8ccd1ed92ace1d41f0d8de8957"), new ByteArray(Hexify.hexify(result)));
	}

	@Test
	public void testGetResult_I4096() {
		ByteArray result = PBKDF2.encode(new SafeByteArray("password"), new ByteArray("salt"), 4096, crypto);

		assertEquals(new ByteArray("4b007901b765489abead49d926f721d065a429c1"), new ByteArray(Hexify.hexify(result)));
	}	
}
