/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.parser.payloadparsers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import com.isode.stroke.elements.UserTune;
import com.isode.stroke.parser.payloadparsers.UserTuneParser;
import com.isode.stroke.parser.payloadparsers.PayloadsParserTester;
import com.isode.stroke.eventloop.DummyEventLoop;

public class UserTuneParserTest {

	public UserTuneParserTest() {

	}

	@Test
	public void testParse_with_all_variables() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse("<tune xmlns=\"http://jabber.org/protocol/tune\">" +
								"<rating>5</rating><title>Minion</title><track>Yellow</track><artist>Ice</artist><URI>Fire</URI><source>Origin</source><length>226</length></tune>"));

		UserTune payload = (UserTune)parser.getPayload();
		assertEquals(Integer.valueOf(5), payload.getRating());
		assertEquals("Minion", payload.getTitle());
		assertEquals("Yellow", payload.getTrack());
		assertEquals("Ice", payload.getArtist());
		assertEquals("Fire", payload.getURI());
		assertEquals("Origin", payload.getSource());
		assertEquals(Integer.valueOf(226), payload.getLength());
	}

	@Test
	public void testParse_with_some_variables() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse("<tune xmlns=\"http://jabber.org/protocol/tune\">" +
								"<title>Minion</title><track>Yellow</track><source>Origin</source><length>226</length></tune>"));

		UserTune payload = (UserTune)parser.getPayload();
		assertNull(payload.getRating());
		assertEquals("Minion", payload.getTitle());
		assertEquals("Yellow", payload.getTrack());
		assertNull(payload.getArtist());
		assertNull(payload.getURI());
		assertEquals("Origin", payload.getSource());
		assertEquals(Integer.valueOf(226), payload.getLength());
	}
}