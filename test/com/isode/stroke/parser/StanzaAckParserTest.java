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
import com.isode.stroke.parser.StanzaAckParser;
import com.isode.stroke.parser.PayloadParserFactoryCollection;
import com.isode.stroke.parser.ElementParserTester;

public class StanzaAckParserTest {

	public StanzaAckParserTest() {

	}

	@Test
	public void testParse() {
		StanzaAckParser testling = new StanzaAckParser();
		ElementParserTester parser = new ElementParserTester(testling);

		assertTrue(parser.parse("<a h=\"12\" xmlns=\"urn:xmpp:sm:2\"/>"));

		assertTrue(testling.getElementGeneric().isValid());
		assertEquals(12, testling.getElementGeneric().getHandledStanzasCount());
	}

	@Test
	public void testParse_Invalid() {
		StanzaAckParser testling = new StanzaAckParser();
		ElementParserTester parser = new ElementParserTester(testling);

		assertTrue(parser.parse("<a h=\"invalid\" xmlns=\"urn:xmpp:sm:2\"/>"));

		assertFalse(testling.getElementGeneric().isValid());
	}

	@Test
	public void testParse_Empty() {
		StanzaAckParser testling = new StanzaAckParser();
		ElementParserTester parser = new ElementParserTester(testling);

		assertTrue(parser.parse("<a xmlns=\"urn:xmpp:sm:2\"/>"));

		assertFalse(testling.getElementGeneric().isValid());
	}
}