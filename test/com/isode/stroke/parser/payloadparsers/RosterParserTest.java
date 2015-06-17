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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import com.isode.stroke.elements.RosterPayload;
import com.isode.stroke.elements.RosterItemPayload;
import com.isode.stroke.parser.payloadparsers.RosterParser;
import com.isode.stroke.parser.payloadparsers.PayloadsParserTester;
import com.isode.stroke.eventloop.DummyEventLoop;
import com.isode.stroke.jid.JID;
import java.util.ArrayList;
import java.util.List;

public class RosterParserTest {

	public RosterParserTest() {

	}

	@Test
	public void testParse() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse(
			"<query xmlns='jabber:iq:roster'>" +
			"	<item jid='foo@bar.com' name='Foo @ Bar' subscription='from' ask='subscribe'>" +
			"		<group>Group 1</group>" +
			"		<group>Group 2</group>" +
			"	</item>" +
			" <item jid='baz@blo.com' name='Baz'/>" +
			"</query>"));

		RosterPayload payload = (RosterPayload)(parser.getPayload());

		assertNull(payload.getVersion());
		List<RosterItemPayload> items = payload.getItems();

		assertEquals(2, items.size());

		assertEquals(new JID("foo@bar.com"), items.get(0).getJID());
		assertEquals("Foo @ Bar", items.get(0).getName());
		assertEquals(RosterItemPayload.Subscription.From, items.get(0).getSubscription());
		assertTrue(items.get(0).getSubscriptionRequested());
		assertEquals(2, items.get(0).getGroups().size());
		assertEquals("Group 1", items.get(0).getGroups().toArray()[0]);
		assertEquals("Group 2", items.get(0).getGroups().toArray()[1]);

		assertEquals(new JID("baz@blo.com"), items.get(1).getJID());
		assertEquals("Baz", items.get(1).getName());
		assertEquals(RosterItemPayload.Subscription.None, items.get(1).getSubscription());
		assertFalse(items.get(1).getSubscriptionRequested());
		assertEquals(0, items.get(1).getGroups().size());
	}

	@Test
	public void testParse_ItemWithUnknownContent() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse(
			"<query xmlns='jabber:iq:roster'>" +
			"	<item jid='foo@bar.com' name='Foo @ Bar' subscription='from' ask='subscribe'>" +
			"		<group>Group 1</group>" +
			"		<foo xmlns=\"http://example.com\"><bar>Baz</bar></foo>" +
			"		<group>Group 2</group>" +
			"		<baz><fum>foo</fum></baz>" +
			"	</item>" +
			"</query>"));

		RosterPayload payload = (RosterPayload)(parser.getPayload());
		List<RosterItemPayload> items = payload.getItems();

		assertEquals(1, items.size());
		assertEquals("Group 1", items.get(0).getGroups().toArray()[0]);
		assertEquals("Group 2", items.get(0).getGroups().toArray()[1]);
		assertEquals(
			"<foo xmlns=\"http://example.com\"><bar xmlns=\"http://example.com\">Baz</bar></foo>" +
			"<baz xmlns=\"jabber:iq:roster\"><fum xmlns=\"jabber:iq:roster\">foo</fum></baz>"
			, items.get(0).getUnknownContent());
		}

	@Test
	public void testParse_WithVersion() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse("<query xmlns='jabber:iq:roster' ver='ver10'/>"));

		RosterPayload payload = (RosterPayload)(parser.getPayload());
		assertNotNull(payload.getVersion());
		assertEquals("ver10", payload.getVersion());
		}

	@Test
	public void testParse_WithEmptyVersion() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse("<query xmlns='jabber:iq:roster' ver=''/>"));

		RosterPayload payload = (RosterPayload)(parser.getPayload());
		assertNotNull(payload.getVersion());
		assertEquals("", payload.getVersion());
		}
}