/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.serializer.payloadserializers;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.isode.stroke.serializer.payloadserializers.RosterSerializer;
import com.isode.stroke.elements.RosterPayload;
import com.isode.stroke.elements.RosterItemPayload;
import com.isode.stroke.jid.JID;

public class RosterSerializerTest {

	/**
	* Default Constructor.
	*/
	public RosterSerializerTest() {

	}

	@Test
	public void testSerialize() {
		RosterSerializer testling = new RosterSerializer();
		RosterPayload roster = new RosterPayload();

		RosterItemPayload item1 = new RosterItemPayload();
		item1.setJID(new JID("foo@bar.com"));
		item1.setName("Foo @ Bar");
		item1.setSubscription(RosterItemPayload.Subscription.From);
		item1.addGroup("Group 1");
		item1.addGroup("Group 2");
		item1.setSubscriptionRequested();
		roster.addItem(item1);

		RosterItemPayload item2 = new RosterItemPayload();
		item2.setJID(new JID("baz@blo.com"));
		item2.setName("Baz");
		roster.addItem(item2);

		String expectedResult = 
			"<query xmlns=\"jabber:iq:roster\">" +
				"<item ask=\"subscribe\" jid=\"foo@bar.com\" name=\"Foo @ Bar\" subscription=\"from\">" +
					"<group>Group 1</group>" +
					"<group>Group 2</group>" +
				"</item>" +
				"<item jid=\"baz@blo.com\" name=\"Baz\" subscription=\"none\"/>" +
			"</query>";

		assertEquals(expectedResult, testling.serialize(roster));
	}

	@Test
	public void testSerialize_ItemWithUnknownContent() {
		RosterSerializer testling = new RosterSerializer();
		RosterPayload roster = new RosterPayload();

		RosterItemPayload item = new RosterItemPayload();
		item.setJID(new JID("baz@blo.com"));
		item.setName("Baz");
		item.addGroup("Group 1");
		item.addGroup("Group 2");
		item.addUnknownContent(
			"<foo xmlns=\"http://example.com\"><bar xmlns=\"http://example.com\">Baz</bar></foo>" +
			"<baz xmlns=\"jabber:iq:roster\"><fum xmlns=\"jabber:iq:roster\">foo</fum></baz>");
		roster.addItem(item);

		String expectedResult = 
			"<query xmlns=\"jabber:iq:roster\">" +
				"<item jid=\"baz@blo.com\" name=\"Baz\" subscription=\"none\">" +
					"<group>Group 1</group>" +
					"<group>Group 2</group>" +
					"<foo xmlns=\"http://example.com\"><bar xmlns=\"http://example.com\">Baz</bar></foo>" +
					"<baz xmlns=\"jabber:iq:roster\"><fum xmlns=\"jabber:iq:roster\">foo</fum></baz>" +
				"</item>" +
			"</query>";

		assertEquals(expectedResult, testling.serialize(roster));
	}

	@Test
	public void testSerialize_WithVersion() {
		RosterSerializer testling = new RosterSerializer();
		RosterPayload roster = new RosterPayload();
		roster.setVersion("ver20");

		String expectedResult = "<query ver=\"ver20\" xmlns=\"jabber:iq:roster\"/>";

		assertEquals(expectedResult, testling.serialize(roster));
	}

	@Test
	public void testSerialize_WithEmptyVersion() {
		RosterSerializer testling = new RosterSerializer();
		RosterPayload roster = new RosterPayload();
		roster.setVersion("");

		String expectedResult = "<query ver=\"\" xmlns=\"jabber:iq:roster\"/>";

		assertEquals(expectedResult, testling.serialize(roster));
	}
}