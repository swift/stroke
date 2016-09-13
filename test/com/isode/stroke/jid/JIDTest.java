/*
 * Copyright (c) 2010-2016 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.jid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class JIDTest {

	public JIDTest() {

	}

	@Test
	public void testConstructorWithString() {
		JID testling = new JID("foo@bar/baz");

		assertEquals("foo", testling.getNode());
		assertEquals("bar", testling.getDomain());
		assertEquals("baz", testling.getResource());
		assertFalse(testling.isBare());
		assertTrue(testling.isValid());
	}


	@Test
	public void testConstructorWithString_NoResource() {
		JID testling = new JID("foo@bar");

		assertEquals("foo", testling.getNode());
		assertEquals("bar", testling.getDomain());
		assertEquals("", testling.getResource());
		assertTrue(testling.isBare());
		assertTrue(testling.isValid());
	}


	@Test
	public void testConstructorWithString_EmptyResource() {
		JID testling = new JID("bar/");

		assertFalse(testling.isValid());
		assertFalse(testling.isBare());
	}


	@Test
	public void testConstructorWithString_NoNode() {
		JID testling = new JID("bar/baz");

		assertEquals("", testling.getNode());
		assertEquals("bar", testling.getDomain());
		assertEquals("baz", testling.getResource());
		assertFalse(testling.isBare());
		assertTrue(testling.isValid());
	}


	@Test
	public void testConstructorWithString_OnlyDomain() {
		JID testling = new JID("bar");

		assertEquals("", testling.getNode());
		assertEquals("bar", testling.getDomain());
		assertEquals("", testling.getResource());
		assertTrue(testling.isBare());
		assertTrue(testling.isValid());
	}

		
	@Test
	public void testConstructorWithString_InvalidDomain() {
		assertFalse(new JID("foo@bar,baz").isValid());
	}

	
	@Test
	public void testConstructorWithString_UpperCaseNode() {
		JID testling = new JID("FoΩ@bar");

		assertEquals("foω", testling.getNode());
		assertEquals("bar", testling.getDomain());
	}

	
	@Test
	public void testConstructorWithString_UpperCaseDomain() {
		JID testling = new JID("FoΩ");

		assertEquals("foω", testling.getDomain());
		assertTrue(testling.isValid());
	}

	
	@Test
	public void testConstructorWithString_UpperCaseResource() {
		JID testling = new JID("bar/FoΩ");

		assertEquals(testling.getResource(), "FoΩ");
		assertTrue(testling.isValid());
	}


	@Test
	public void testConstructorWithString_EmptyNode() {
		JID testling = new JID("@bar");

		assertFalse(testling.isValid());
	}

	
	@Test
	public void testConstructorWithString_IllegalResource() {
		JID testling = new JID("foo@bar.com/رمقه ترنس ");

		assertFalse(testling.isValid());
	}

	
	@Test
	public void testConstructorWithString_SpacesInNode() {
		assertFalse(new JID("   alice@wonderland.lit").isValid());
		assertFalse(new JID("alice   @wonderland.lit").isValid());
	}
		
	
	@Test
	public void testConstructorWithStrings() {
		JID testling = new JID("foo", "bar", "baz");

		assertEquals("foo", testling.getNode());
		assertEquals("bar", testling.getDomain());
		assertEquals("baz", testling.getResource());
		assertTrue(testling.isValid());
	}


	@Test
	public void testConstructorWithStrings_EmptyDomain() {
		JID testling = new JID("foo", "", "baz");

		assertFalse(testling.isValid());
	}
	
	@Test
	public void testConstructorWithStrings_EmptyResource() {
	    JID testling = new JID("foo","bar","");
	    assertFalse(testling.isValid());
	}

	
	@Test
	public void testIsBare() {
		assertTrue(new JID("foo@bar").isBare());
	}

	
	@Test
	public void testIsBare_NotBare() {
		assertFalse(new JID("foo@bar/baz").isBare());
	}

	
	@Test
	public void testToBare() {
		JID testling = new JID("foo@bar/baz");

		assertEquals("foo", testling.toBare().getNode());
		assertEquals("bar", testling.toBare().getDomain());
		assertTrue(testling.toBare().isBare());
	}

	
	@Test
	public void testToBare_EmptyNode() {
		JID testling = new JID("bar/baz");

		assertEquals("", testling.toBare().getNode());
		assertEquals("bar", testling.toBare().getDomain());
		assertTrue(testling.toBare().isBare());
		assertTrue(testling.isValid());
	}


	@Test
	public void testToBare_EmptyResource() {
		JID testling = new JID("bar/");

		assertEquals("", testling.toBare().getNode());
		assertEquals("bar", testling.toBare().getDomain());
		assertTrue(testling.toBare().isBare());
	}


	@Test
	public void testToString() {
		JID testling = new JID("foo@bar/baz");

		assertEquals("foo@bar/baz", testling.toString());
	}


	@Test
	public void testToString_EmptyNode() {
		JID testling = new JID("bar/baz");

		assertEquals("bar/baz", testling.toString());
	}


	@Test
	public void testToString_NoResource() {
		JID testling = new JID("foo@bar");

		assertEquals("foo@bar", testling.toString());
	}


	@Test
	public void testToString_EmptyResource() {
		JID testling = new JID("foo@bar/");

		assertEquals("foo@bar/", testling.toString());
	}


	@Test
	public void testCompare_SmallerNode() {
		JID testling1 = new JID("a@c");
		JID testling2 = new JID("b@b");

		assertEquals(-1, testling1.compare(testling2, JID.CompareType.WithResource));
	}


	@Test
	public void testCompare_LargerNode() {
		JID testling1 = new JID("c@a");
		JID testling2 = new JID("b@b");

		assertEquals(1, testling1.compare(testling2, JID.CompareType.WithResource));
	}

	
	@Test
	public void testCompare_SmallerDomain() {
		JID testling1 = new JID("x@a/c");
		JID testling2 = new JID("x@b/b");

		assertEquals(-1, testling1.compare(testling2, JID.CompareType.WithResource));
	}

	
	@Test
	public void testCompare_LargerDomain() {
		JID testling1 = new JID("x@b/b");
		JID testling2 = new JID("x@a/c");

		assertEquals(1, testling1.compare(testling2, JID.CompareType.WithResource));
	}

	
	@Test
	public void testCompare_SmallerResource() {
		JID testling1 = new JID("x@y/a");
		JID testling2 = new JID("x@y/b");

		assertEquals(-1, testling1.compare(testling2, JID.CompareType.WithResource));
	}

	
	@Test
	public void testCompare_LargerResource() {
		JID testling1 = new JID("x@y/b");
		JID testling2 = new JID("x@y/a");

		assertEquals(1, testling1.compare(testling2, JID.CompareType.WithResource));
	}

	
	@Test
	public void testCompare_Equal() {
		JID testling1 = new JID("x@y/z");
		JID testling2 = new JID("x@y/z");

		assertEquals(0, testling1.compare(testling2, JID.CompareType.WithResource));
	}

	
	@Test
	public void testCompare_EqualWithoutResource() {
		JID testling1 = new JID("x@y/a");
		JID testling2 = new JID("x@y/b");

		assertEquals(0, testling1.compare(testling2, JID.CompareType.WithoutResource));
	}

	
	@Test
	public void testCompare_NoResourceAndEmptyResource() {
		JID testling1 = new JID("x@y/");
		JID testling2 = new JID("x@y");

		assertEquals(1, testling1.compare(testling2, JID.CompareType.WithResource));
	}

	
	@Test
	public void testCompare_EmptyResourceAndNoResource() {
		JID testling1 = new JID("x@y");
		JID testling2 = new JID("x@y/");

		assertEquals(-1, testling1.compare(testling2, JID.CompareType.WithResource));
	}

		
	@Test
	public void testEquals() {
		JID testling1 = new JID("x@y/c");
		JID testling2 = new JID("x@y/c");

		assertTrue(testling1.compare(testling2, JID.CompareType.WithResource) == 0);
	}

		
	@Test
	public void testEquals_NotEqual() {
		JID testling1 = new JID("x@y/c");
		JID testling2 = new JID("x@y/d");

		assertFalse(testling1.compare(testling2, JID.CompareType.WithResource) == 0);
	}

		
	@Test
	public void testEquals_WithoutResource() {
		JID testling1 = new JID("x@y/c");
		JID testling2 = new JID("x@y/d");

		assertTrue(testling1.compare(testling2, JID.CompareType.WithoutResource) == 0);
	}

		
	@Test
	public void testSmallerThan() {
		JID testling1 = new JID("x@y/c");
		JID testling2 = new JID("x@y/d");

		assertTrue(testling1.compare(testling2, JID.CompareType.WithResource) < 0);
	}

		
	@Test
	public void testSmallerThan_Equal() {
		JID testling1 = new JID("x@y/d");
		JID testling2 = new JID("x@y/d");

		assertFalse(testling1.compare(testling2, JID.CompareType.WithResource) < 0);
	}

		
	@Test
	public void testSmallerThan_Larger() {
		JID testling1 = new JID("x@y/d");
		JID testling2 = new JID("x@y/c");

		assertFalse(testling1.compare(testling2, JID.CompareType.WithResource) < 0);
	}

		
	@Test
	public void testHasResource() {
		JID testling = new JID("x@y/d");

		assertFalse(testling.isBare());
	}

		
	@Test
	public void testHasResource_NoResource() {
		JID testling = new JID("x@y");

		assertTrue(testling.isBare());
	}


	@Test
	public void testGetEscapedNode() {
		String escaped = JID.getEscapedNode("alice@wonderland.lit");
		assertEquals("alice\\40wonderland.lit", escaped);

		escaped = JID.getEscapedNode("\\& \" ' / <\\\\> @ :\\3a\\40");
		assertEquals("\\\\26\\20\\22\\20\\27\\20\\2f\\20\\3c\\\\\\3e\\20\\40\\20\\3a\\5c3a\\5c40", escaped);
	}

	
	@Test
	public void testGetEscapedNode_XEP106Examples() {
		assertEquals("\\2plus\\2is\\4", JID.getEscapedNode("\\2plus\\2is\\4"));
		assertEquals("foo\\bar", JID.getEscapedNode("foo\\bar"));
		assertEquals("foob\\41r", JID.getEscapedNode("foob\\41r"));
		assertEquals(JID.getEscapedNode("space cadet"), "space\\20cadet");
		assertEquals(JID.getEscapedNode("call me \"ishmael\""), "call\\20me\\20\\22ishmael\\22");
		assertEquals(JID.getEscapedNode("at&t guy"), "at\\26t\\20guy");
		assertEquals(JID.getEscapedNode("d'artagnan"), "d\\27artagnan");
		assertEquals(JID.getEscapedNode("/.fanboy"), "\\2f.fanboy");
		assertEquals(JID.getEscapedNode("::foo::"), "\\3a\\3afoo\\3a\\3a");
		assertEquals(JID.getEscapedNode("<foo>"), "\\3cfoo\\3e");
		assertEquals(JID.getEscapedNode("user@host"), "user\\40host");
		assertEquals(JID.getEscapedNode("c:\\net"), "c\\3a\\net");
		assertEquals(JID.getEscapedNode("c:\\\\net"), "c\\3a\\\\net");
		assertEquals(JID.getEscapedNode("c:\\cool stuff"), "c\\3a\\cool\\20stuff");
		assertEquals(JID.getEscapedNode("c:\\5commas"), "c\\3a\\5c5commas");
	}

	
	@Test
	public void testGetEscapedNode_BackslashAtEnd() {
		assertEquals("foo\\", JID.getEscapedNode("foo\\"));
	}


	@Test
	public void testGetUnescapedNode() {
		String input = "\\& \" ' / <\\\\> @ : \\5c\\40";
		JID testling = new JID((JID.getEscapedNode(input) + "@y"));
		assertTrue(testling.isValid());
		assertEquals(input, testling.getUnescapedNode());
	}

	@Test
	public void testGetUnescapedNode_XEP106Examples() {
		assertEquals("\\2plus\\2is\\4", new JID("\\2plus\\2is\\4@example.com").getUnescapedNode());
		assertEquals("foo\\bar", new JID("foo\\bar@example.com").getUnescapedNode());
		assertEquals("foob\\41r", new JID("foob\\41r@example.com").getUnescapedNode());
		assertEquals("space cadet", new JID("space\\20cadet@example.com").getUnescapedNode());
		assertEquals("call me \"ishmael\"", new JID("call\\20me\\20\\22ishmael\\22@example.com").getUnescapedNode());
		assertEquals("at&t guy", new JID("at\\26t\\20guy@example.com").getUnescapedNode());
		assertEquals("d'artagnan", new JID("d\\27artagnan@example.com").getUnescapedNode());
		assertEquals("/.fanboy", new JID("\\2f.fanboy@example.com").getUnescapedNode());
		assertEquals("::foo::", new JID("\\3a\\3afoo\\3a\\3a@example.com").getUnescapedNode());
		assertEquals("<foo>", new JID("\\3cfoo\\3e@example.com").getUnescapedNode());
		assertEquals("user@host", new JID("user\\40host@example.com").getUnescapedNode());
		assertEquals("c:\\net", new JID("c\\3a\\net@example.com").getUnescapedNode());
		assertEquals("c:\\\\net", new JID("c\\3a\\\\net@example.com").getUnescapedNode());
		assertEquals("c:\\cool stuff", new JID("c\\3a\\cool\\20stuff@example.com").getUnescapedNode());
		assertEquals("c:\\5commas", new JID("c\\3a\\5c5commas@example.com").getUnescapedNode());
	}
}