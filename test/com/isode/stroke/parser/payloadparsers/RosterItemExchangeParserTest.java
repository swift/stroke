/*
 * Copyright (c) 2011 Jan Kaluza
 * Licensed under the Simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
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
import com.isode.stroke.elements.RosterItemExchangePayload;
import com.isode.stroke.parser.payloadparsers.RosterItemExchangeParser;
import com.isode.stroke.parser.payloadparsers.PayloadsParserTester;
import com.isode.stroke.eventloop.DummyEventLoop;
import com.isode.stroke.jid.JID;
import java.util.Vector;

public class RosterItemExchangeParserTest {

	public RosterItemExchangeParserTest() {

	}

	@Test
	public void testParse() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse("<x xmlns=\"http://jabber.org/protocol/rosterx\">" +
					"<item action=\"add\" jid=\"foo@bar.com\" name=\"Foo @ Bar\">" +
						"<group>Group 1</group>" +
						"<group>Group 2</group>" +
					"</item>" +
					"<item action=\"modify\" jid=\"baz@blo.com\" name=\"Baz\"/>" +
				"</x>"));

		RosterItemExchangePayload payload = (RosterItemExchangePayload)parser.getPayload();
		Vector<RosterItemExchangePayload.Item> items = payload.getItems();

		assertEquals(2, items.size());

		assertEquals(new JID("foo@bar.com"), items.get(0).getJID());
		assertEquals("Foo @ Bar", items.get(0).getName());
		assertEquals(RosterItemExchangePayload.Item.Action.Add, items.get(0).getAction());
		assertEquals(2, items.get(0).getGroups().size());
		assertEquals("Group 1", items.get(0).getGroups().get(0));
		assertEquals("Group 2", items.get(0).getGroups().get(1));

		assertEquals(new JID("baz@blo.com"), items.get(1).getJID());
		assertEquals("Baz", items.get(1).getName());
		assertEquals(RosterItemExchangePayload.Item.Action.Modify, items.get(1).getAction());
		assertEquals(0, items.get(1).getGroups().size());
	}
}