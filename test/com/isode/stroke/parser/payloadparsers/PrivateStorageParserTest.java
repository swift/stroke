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
import org.junit.Test;
import com.isode.stroke.elements.PrivateStorage;
import com.isode.stroke.elements.Storage;
import com.isode.stroke.parser.payloadparsers.PrivateStorageParser;
import com.isode.stroke.parser.payloadparsers.PayloadsParserTester;
import com.isode.stroke.parser.payloadparsers.PayloadParserTester;
import com.isode.stroke.parser.PayloadParserFactoryCollection;
import com.isode.stroke.eventloop.DummyEventLoop;
import com.isode.stroke.jid.JID;

public class PrivateStorageParserTest {

	public PrivateStorageParserTest() {

	}

	@Test
	public void testParse() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);

		assertNotNull(parser.parse(
			"<query xmlns='jabber:iq:private'>"
		+		"<storage xmlns='storage:bookmarks'>"
		+			"<conference name='Swift' jid='swift@rooms.swift.im'>"
		+				"<nick>Alice</nick>"
		+			"</conference>"
		+		"</storage>"
		+	"</query>"));

		PrivateStorage payload = (PrivateStorage)(parser.getPayload());
		assertNotNull(payload);
		Storage storage = (Storage)(payload.getPayload());
		assertNotNull(storage);
		assertEquals("Alice", storage.getRooms().get(0).nick);
		assertEquals(new JID("swift@rooms.swift.im"), storage.getRooms().get(0).jid);
	}

	@Test
	public void testParse_NoPayload() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);

		assertNotNull(parser.parse("<query xmlns='jabber:iq:private'/>"));

		PrivateStorage payload = (PrivateStorage)(parser.getPayload());
		assertNotNull(payload);
		assertNull(payload.getPayload());
	}

	@Test
	public void testParse_MultiplePayloads() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);

		assertNotNull(parser.parse(
			"<query xmlns='jabber:iq:private'>"
		+		"<storage xmlns='storage:bookmarks'>"
		+			"<conference name='Swift' jid='swift@rooms.swift.im'>"
		+				"<nick>Alice</nick>"
		+			"</conference>"
		+		"</storage>"
		+		"<storage xmlns='storage:bookmarks'>"
		+			"<conference name='Swift' jid='swift@rooms.swift.im'>"
		+				"<nick>Rabbit</nick>"
		+			"</conference>"
		+		"</storage>"
		+	"</query>"));

		PrivateStorage payload = (PrivateStorage)(parser.getPayload());
		assertNotNull(payload);
		Storage storage = (Storage)(payload.getPayload());
		assertNotNull(storage);
		assertEquals("Rabbit", storage.getRooms().get(0).nick);
	}

	@Test
	public void testParse_UnsupportedPayload() {
		PayloadParserFactoryCollection factories = new PayloadParserFactoryCollection();
		PrivateStorageParser testling = new PrivateStorageParser(factories);
		PayloadParserTester parser = new PayloadParserTester(testling);

		assertNotNull(parser.parse(
			"<query xmlns='jabber:iq:private'>" +
				"<foo>Bar</foo>" +
			"</query>"));

		assertNull(((PrivateStorage)(testling.getPayload())).getPayload());
	}
}