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

package com.isode.stroke.serializer.payloadserializers;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.isode.stroke.serializer.payloadserializers.RosterItemExchangeSerializer;
import com.isode.stroke.elements.RosterItemExchangePayload;
import com.isode.stroke.jid.JID;

public class RosterItemExchangeSerializerTest {

	/**
	* Default Constructor.
	*/
	public RosterItemExchangeSerializerTest() {

	}

	@Test
	public void testSerialize() {
		RosterItemExchangeSerializer testling = new RosterItemExchangeSerializer();
		RosterItemExchangePayload roster = new RosterItemExchangePayload();
		RosterItemExchangePayload.Item item1 = new RosterItemExchangePayload.Item();
		item1.setJID(new JID("foo@bar.com"));
		item1.setName("Foo @ Bar");
		item1.setAction(RosterItemExchangePayload.Item.Action.Add);
		item1.addGroup("Group 1");
		item1.addGroup("Group 2");
		roster.addItem(item1);

		RosterItemExchangePayload.Item item2 = new RosterItemExchangePayload.Item();
		item2.setJID(new JID("baz@blo.com"));
		item2.setName("Baz");
		item2.setAction(RosterItemExchangePayload.Item.Action.Modify);
		roster.addItem(item2);

		String expectedResult = 
			"<x xmlns=\"http://jabber.org/protocol/rosterx\">" +
				"<item action=\"add\" jid=\"foo@bar.com\" name=\"Foo @ Bar\">" +
					"<group>Group 1</group>" +
					"<group>Group 2</group>" +
				"</item>" +
				"<item action=\"modify\" jid=\"baz@blo.com\" name=\"Baz\"/>" +
			"</x>";

		assertEquals(expectedResult, testling.serialize(roster));
	}
}