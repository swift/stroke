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

package com.isode.stroke.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import com.isode.stroke.parser.AttributeMap;

public class AttributeMapTest {

	public AttributeMapTest() {

	}

	@Test
	public void testGetAttribute_Namespaced() {
		AttributeMap testling = new AttributeMap();
		testling.addAttribute("lang", "", "nl");
		testling.addAttribute("lang", "http://www.w3.org/XML/1998/namespace", "en");
		testling.addAttribute("lang", "", "fr");

		assertEquals("en", testling.getAttribute("lang", "http://www.w3.org/XML/1998/namespace"));
	}

	@Test
	public void testGetBoolAttribute_True() {
		AttributeMap testling = new AttributeMap();
		testling.addAttribute("foo", "", "true");

		assertTrue(testling.getBoolAttribute("foo"));
	}

	@Test
	public void testGetBoolAttribute_1() {
		AttributeMap testling = new AttributeMap();
		testling.addAttribute("foo", "", "1");

		assertTrue(testling.getBoolAttribute("foo"));
	}

	@Test
	public void testGetBoolAttribute_False() {
		AttributeMap testling = new AttributeMap();
		testling.addAttribute("foo", "", "false");

		assertFalse(testling.getBoolAttribute("foo", true));
	}

	@Test
	public void testGetBoolAttribute_0() {
		AttributeMap testling = new AttributeMap();
		testling.addAttribute("foo", "", "0");

		assertFalse(testling.getBoolAttribute("foo", true));
	}

	@Test
	public void testGetBoolAttribute_Invalid() {
		AttributeMap testling = new AttributeMap();
		testling.addAttribute("foo", "", "bla");

		assertFalse(testling.getBoolAttribute("foo", true));
	}

	@Test
	public void testGetBoolAttribute_UnknownWithDefaultTrue() {
		AttributeMap testling = new AttributeMap();

		assertTrue(testling.getBoolAttribute("foo", true));
	}

	@Test
	public void testGetBoolAttribute_UnknownWithDefaultFalse() {
		AttributeMap testling = new AttributeMap();

		assertFalse(testling.getBoolAttribute("foo", false));
	}
}