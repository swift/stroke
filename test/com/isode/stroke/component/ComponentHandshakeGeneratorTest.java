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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import org.junit.Before;
import com.isode.stroke.component.ComponentHandshakeGenerator;
import com.isode.stroke.crypto.JavaCryptoProvider;
import com.isode.stroke.crypto.CryptoProvider;

public class ComponentHandshakeGeneratorTest {

	public ComponentHandshakeGeneratorTest() {

	}

	private CryptoProvider crypto;

	@Before
	public void setUp() {
		crypto = new JavaCryptoProvider();
	}

	@Test
	public void testGetHandshake() {
		String result = ComponentHandshakeGenerator.getHandshake("myid", "mysecret", crypto);
		assertEquals("4011cd31f9b99ac089a0cd7ce297da7323fa2525", result);
	}

	@Test
	public void testGetHandshake_SpecialChars() {
		String result = ComponentHandshakeGenerator.getHandshake("&<", ">'\"", crypto);
		assertEquals("33631b3e0aaeb2a11c4994c917919324028873fe", result);
	}	
}