/*
 * Copyright (c) 2014 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */

/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.serializer.payloadserializers;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.isode.stroke.serializer.payloadserializers.PubSubItemSerializer;
import com.isode.stroke.serializer.payloadserializers.FullPayloadSerializerCollection;
import com.isode.stroke.serializer.PayloadSerializerCollection;
import com.isode.stroke.elements.PubSubItem;
import com.isode.stroke.elements.RawXMLPayload;

public class PubSubItemSerializerTest {

	private FullPayloadSerializerCollection serializers = new FullPayloadSerializerCollection();

	/**
	* Default Constructor.
	*/
	public PubSubItemSerializerTest() {

	}

	@Test
	public void testSerialize() {
		PubSubItemSerializer serializer = new PubSubItemSerializer(serializers);

		RawXMLPayload payload = new RawXMLPayload();
		payload.setRawXML("<payload xmlns=\"tmp\"/>");

		PubSubItem item = new PubSubItem();
		item.addData(payload);
		item.setID("pubsub-item-1");

		String expectedResult = 
			"<item id=\"pubsub-item-1\" xmlns=\"http://jabber.org/protocol/pubsub\">"
		+		"<payload xmlns=\"tmp\"/>"
		+	"</item>";

		assertEquals(expectedResult, serializer.serialize(item));
	}

	@Test
	public void testSerializeEmptyID() {
		PubSubItemSerializer serializer = new PubSubItemSerializer(serializers);

		PubSubItem item = new PubSubItem();

		String expectedResult = 
			"<item xmlns=\"http://jabber.org/protocol/pubsub\"/>";

		assertEquals(expectedResult, serializer.serialize(item));
	}

}