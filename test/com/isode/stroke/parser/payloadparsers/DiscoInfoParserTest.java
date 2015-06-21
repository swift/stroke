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

package com.isode.stroke.parser.payloadparsers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import com.isode.stroke.elements.DiscoInfo;
import com.isode.stroke.parser.payloadparsers.DiscoInfoParser;
import com.isode.stroke.parser.payloadparsers.PayloadsParserTester;
import com.isode.stroke.eventloop.DummyEventLoop;

public class DiscoInfoParserTest {

	public DiscoInfoParserTest() {

	}

	@Test
	public void testParse() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);

		assertNotNull(parser.parse(
			"<query xmlns=\"http://jabber.org/protocol/disco#info\">" +
				"<identity name=\"Swift\" category=\"client\" type=\"pc\" xml:lang=\"en\"/>" + 
				"<identity name=\"Vlug\" category=\"client\" type=\"pc\" xml:lang=\"nl\"/>" +
				"<feature var=\"foo-feature\"/>" +
				"<feature var=\"bar-feature\"/>" +
				"<feature var=\"baz-feature\"/>" +
			"</query>"));

		DiscoInfo payload = (DiscoInfo)(parser.getPayload());
		assertEquals(2, payload.getIdentities().size());
		assertEquals("Swift", payload.getIdentities().get(0).getName());
		assertEquals("pc", payload.getIdentities().get(0).getType());
		assertEquals("client", payload.getIdentities().get(0).getCategory());
		assertEquals("en", payload.getIdentities().get(0).getLanguage());
		assertEquals("Vlug", payload.getIdentities().get(1).getName());
		assertEquals("pc", payload.getIdentities().get(1).getType());
		assertEquals("client", payload.getIdentities().get(1).getCategory());
		assertEquals("nl", payload.getIdentities().get(1).getLanguage());
		assertEquals(3, payload.getFeatures().size());
		assertEquals("foo-feature", payload.getFeatures().get(0));
		assertEquals("bar-feature", payload.getFeatures().get(1));
		assertEquals("baz-feature", payload.getFeatures().get(2));
		assertTrue(payload.getNode().isEmpty());
	}

	@Test
	public void testParse_Node() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);

		assertNotNull(parser.parse(
			"<query xmlns=\"http://jabber.org/protocol/disco#info\" node=\"blahblah\">" +
				"<identity name=\"Swift\" category=\"client\" type=\"pc\" xml:lang=\"en\"/>" + 
				"<identity name=\"Vlug\" category=\"client\" type=\"pc\" xml:lang=\"nl\"/>" +
				"<feature var=\"foo-feature\"/>" +
				"<feature var=\"bar-feature\"/>" +
				"<feature var=\"baz-feature\"/>" +
			"</query>"));

		DiscoInfo payload = (DiscoInfo)(parser.getPayload());
		assertEquals(2, payload.getIdentities().size());
		assertEquals("Swift", payload.getIdentities().get(0).getName());
		assertEquals("pc", payload.getIdentities().get(0).getType());
		assertEquals("client", payload.getIdentities().get(0).getCategory());
		assertEquals("en", payload.getIdentities().get(0).getLanguage());
		assertEquals("Vlug", payload.getIdentities().get(1).getName());
		assertEquals("pc", payload.getIdentities().get(1).getType());
		assertEquals("client", payload.getIdentities().get(1).getCategory());
		assertEquals("nl", payload.getIdentities().get(1).getLanguage());
		assertEquals(3, payload.getFeatures().size());
		assertEquals("foo-feature", payload.getFeatures().get(0));
		assertEquals("bar-feature", payload.getFeatures().get(1));
		assertEquals("baz-feature", payload.getFeatures().get(2));
		assertEquals("blahblah", payload.getNode());
	}

	@Test
	public void testParse_Form() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);

		assertNotNull(parser.parse(
			"<query xmlns=\"http://jabber.org/protocol/disco#info\">" +
				"<feature var=\"foo-feature\"/>" +
				"<x type=\"submit\" xmlns=\"jabber:x:data\">" +
					"<title>Bot Configuration</title>" +
					"<instructions>Hello!</instructions>" +
				"</x>" +
				"<feature var=\"bar-feature\"/>" +
			"</query>"));

		DiscoInfo payload = (DiscoInfo)(parser.getPayload());
		assertEquals(1, payload.getExtensions().size());
		assertEquals("Bot Configuration", payload.getExtensions().get(0).getTitle());
		assertEquals(2, payload.getFeatures().size());
		assertEquals("foo-feature", payload.getFeatures().get(0));
		assertEquals("bar-feature", payload.getFeatures().get(1));
	}
}