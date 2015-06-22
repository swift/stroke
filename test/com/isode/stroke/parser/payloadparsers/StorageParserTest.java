/*
 * Copyright (c) 2010-2012 Isode Limited.
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
import com.isode.stroke.elements.Storage;
import com.isode.stroke.parser.payloadparsers.StorageParser;
import com.isode.stroke.parser.payloadparsers.PayloadsParserTester;
import com.isode.stroke.eventloop.DummyEventLoop;
import com.isode.stroke.jid.JID;
import java.util.Vector;

public class StorageParserTest {

	public StorageParserTest() {

	}

	@Test
	public void testParse_Room() {
		DummyEventLoop eventloop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventloop);

		assertNotNull(parser.parse(
			"<storage xmlns='storage:bookmarks'>"
		+		"<conference "
		+				"name='Council of Oberon' "
		+				"autojoin='true' jid='council@conference.underhill.org'>"
		+			"<nick>Puck</nick>"
		+			"<password>MyPass</password>"
		+		"</conference>"
		+	"</storage>"));

		Storage payload = (Storage)(parser.getPayload());
		Vector<Storage.Room> rooms = payload.getRooms();
		assertEquals(1, rooms.size());
		assertEquals("Council of Oberon", rooms.get(0).name);
		assertEquals(new JID("council@conference.underhill.org"), rooms.get(0).jid);
		assertTrue(rooms.get(0).autoJoin);
		assertEquals("Puck", rooms.get(0).nick);
		assertEquals("MyPass", rooms.get(0).password);
	}

	@Test
	public void testParse_MultipleRooms() {
		DummyEventLoop eventloop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventloop);

		assertNotNull(parser.parse(
			"<storage xmlns='storage:bookmarks'>"
		+		"<conference "
		+				"name='Council of Oberon' "
		+				"jid='council@conference.underhill.org' />"
		+		"<conference "
		+				"name='Tea &amp; jam party' "
		+				"jid='teaparty@wonderland.lit' />"
		+	"</storage>"));

		Storage payload = (Storage)(parser.getPayload());
		Vector<Storage.Room> rooms = payload.getRooms();
		assertEquals(2, rooms.size());
		assertEquals("Council of Oberon", rooms.get(0).name);
		assertEquals(new JID("council@conference.underhill.org"), rooms.get(0).jid);
		assertEquals("Tea & jam party", rooms.get(1).name);
		assertEquals(new JID("teaparty@wonderland.lit"), rooms.get(1).jid);
	}

	@Test
	public void testParse_URL() {
		DummyEventLoop eventloop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventloop);

		assertNotNull(parser.parse(
			"<storage xmlns='storage:bookmarks'>"
		+		"<url name='Complete Works of Shakespeare' url='http://the-tech.mit.edu/Shakespeare/'/>"
		+	"</storage>"));

		Storage payload = (Storage)(parser.getPayload());
		Vector<Storage.URL> urls = payload.getURLs();
		assertEquals(1, urls.size());
		assertEquals("Complete Works of Shakespeare", urls.get(0).name);
		assertEquals("http://the-tech.mit.edu/Shakespeare/", urls.get(0).url);
	}
}