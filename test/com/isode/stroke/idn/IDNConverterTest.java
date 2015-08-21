/*
 * Copyright (c) 2010 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.idn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Before;
import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.idn.IDNConverter;
import com.isode.stroke.idn.IDNA;
import com.isode.stroke.idn.ICUConverter;
import com.ibm.icu.text.StringPrepParseException;

public class IDNConverterTest {

	private IDNConverter testling;

	@Before
	public void setUp() {
		testling = new ICUConverter();
	}

	@Test
	public void testStringPrep() {
		try {
			String result = testling.getStringPrepared("tronçon", IDNConverter.StringPrepProfile.NamePrep);
			assertEquals("tronçon", result);
		} catch (IllegalArgumentException e) {
			assertTrue("getStringPrep threw " + e, (e == null));
		}
	}

	@Test
	public void testStringPrep_Empty() {
		try{
			assertEquals("", testling.getStringPrepared("", IDNConverter.StringPrepProfile.NamePrep));
			assertEquals("", testling.getStringPrepared("", IDNConverter.StringPrepProfile.XMPPNodePrep));
			assertEquals("", testling.getStringPrepared("", IDNConverter.StringPrepProfile.XMPPResourcePrep));
		} catch (IllegalArgumentException e) {
			assertTrue("getStringPrep threw " + e, (e == null));
		}
	}

	@Test
	public void testGetEncoded() {
		String result = testling.getIDNAEncoded("www.swift.im");
		assertNotNull(result);
		assertEquals("www.swift.im", result);
	}

	@Test
	public void testGetEncoded_International() {
		String result = testling.getIDNAEncoded("www.tronçon.com");
		assertNotNull(result);
		assertEquals("www.xn--tronon-zua.com", result); 
	}

	@Test
	public void testGetEncoded_Invalid() {
		String result = testling.getIDNAEncoded("www.foo,bar.com");
		assertNull(result);
	}
}