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
import org.junit.Before;
import com.isode.stroke.parser.PayloadParserFactoryCollection;
import com.isode.stroke.parser.StanzaParserTester;
import com.isode.stroke.parser.PresenceParser;
import com.isode.stroke.elements.Presence;

public class PresenceParserTest {

	private PayloadParserFactoryCollection factoryCollection_;

	public PresenceParserTest() {

	}

	@Before
	public void setUp() {
		factoryCollection_ = new PayloadParserFactoryCollection();
	}

	@Test
	public void testParse_Available() {
		PresenceParser testling = new PresenceParser(factoryCollection_);
		StanzaParserTester parser = new StanzaParserTester(testling);

		assertTrue(parser.parse("<presence/>"));

		assertEquals(Presence.Type.Available, testling.getStanzaGeneric().getType());
	}

	@Test
	public void testParse_Unavailable() {
		PresenceParser testling = new PresenceParser(factoryCollection_);
		StanzaParserTester parser = new StanzaParserTester(testling);

		assertTrue(parser.parse("<presence type=\"unavailable\"/>"));

		assertEquals(Presence.Type.Unavailable, testling.getStanzaGeneric().getType());
	}

	@Test
	public void testParse_Probe() {
		PresenceParser testling = new PresenceParser(factoryCollection_);
		StanzaParserTester parser = new StanzaParserTester(testling);

		assertTrue(parser.parse("<presence type=\"probe\"/>"));

		assertEquals(Presence.Type.Probe, testling.getStanzaGeneric().getType());
	}

	@Test
	public void testParse_Subscribe() {
		PresenceParser testling = new PresenceParser(factoryCollection_);
		StanzaParserTester parser = new StanzaParserTester(testling);

		assertTrue(parser.parse("<presence type=\"subscribe\"/>"));

		assertEquals(Presence.Type.Subscribe, testling.getStanzaGeneric().getType());
	}

	@Test
	public void testParse_Subscribed() {
		PresenceParser testling = new PresenceParser(factoryCollection_);
		StanzaParserTester parser = new StanzaParserTester(testling);

		assertTrue(parser.parse("<presence type=\"subscribed\"/>"));

		assertEquals(Presence.Type.Subscribed, testling.getStanzaGeneric().getType());
	}

	@Test
	public void testParse_Unsubscribe() {
		PresenceParser testling = new PresenceParser(factoryCollection_);
		StanzaParserTester parser = new StanzaParserTester(testling);

		assertTrue(parser.parse("<presence type=\"unsubscribe\"/>"));

		assertEquals(Presence.Type.Unsubscribe, testling.getStanzaGeneric().getType());
	}

	@Test
	public void testParse_Unsubscribed() {
		PresenceParser testling = new PresenceParser(factoryCollection_);
		StanzaParserTester parser = new StanzaParserTester(testling);

		assertTrue(parser.parse("<presence type=\"unsubscribed\"/>"));

		assertEquals(Presence.Type.Unsubscribed, testling.getStanzaGeneric().getType());
	}

	@Test
	public void testParse_Error() {
		PresenceParser testling = new PresenceParser(factoryCollection_);
		StanzaParserTester parser = new StanzaParserTester(testling);

		assertTrue(parser.parse("<presence type=\"error\"/>"));

		assertEquals(Presence.Type.Error, testling.getStanzaGeneric().getType());
	}
}