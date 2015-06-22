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
import com.isode.stroke.serializer.payloadserializers.PubSubItemsSerializer;
import com.isode.stroke.serializer.payloadserializers.FullPayloadSerializerCollection;
import com.isode.stroke.serializer.PayloadSerializerCollection;
import com.isode.stroke.elements.PubSubItem;
import com.isode.stroke.elements.PubSubItems;
import com.isode.stroke.elements.RawXMLPayload;

public class PubSubItemsSerializerTest {

	private FullPayloadSerializerCollection serializers = new FullPayloadSerializerCollection();

	/**
	* Default Constructor.
	*/
	public PubSubItemsSerializerTest() {

	}

	@Test
	public void testSerialize() {
		PubSubItemsSerializer serializer = new PubSubItemsSerializer(serializers);

		RawXMLPayload payload1 = new RawXMLPayload();
		payload1.setRawXML("<payload xmlns=\"tmp\"/>");

		PubSubItem item1 = new PubSubItem();
		item1.addData(payload1);
		item1.setID("pubsub-item-1");

		RawXMLPayload payload2 = new RawXMLPayload();
		payload2.setRawXML("<payload xmlns=\"other-tmp\"/>");

		PubSubItem item2 = new PubSubItem();
		item2.addData(payload2);
		item2.setID("pubsub-item-2");

		PubSubItems items = new PubSubItems();
		items.setNode("test-node");
		items.setSubscriptionID("sub-id");
		items.addItem(item1);
		items.addItem(item2);

		String expectedResult = 
			"<items node=\"test-node\" subid=\"sub-id\" xmlns=\"http://jabber.org/protocol/pubsub\">"
		+		"<item id=\"pubsub-item-1\" xmlns=\"http://jabber.org/protocol/pubsub\">"
		+			"<payload xmlns=\"tmp\"/>"
		+		"</item>"
		+		"<item id=\"pubsub-item-2\" xmlns=\"http://jabber.org/protocol/pubsub\">"
		+			"<payload xmlns=\"other-tmp\"/>"
		+		"</item>"
		+	"</items>";

		assertEquals(expectedResult, serializer.serialize(items));
	}

	@Test
	public void testSerializeEmptyItems() {
		PubSubItemsSerializer serializer = new PubSubItemsSerializer(serializers);

		PubSubItems items = new PubSubItems();

		// Swiften code doesn't check for node being null in serializer and therefore will have extra node=\"\",
		// BUT since it is being check in serializer here, there will be no node.
		String expectedResult = 
			"<items xmlns=\"http://jabber.org/protocol/pubsub\"/>";

		assertEquals(expectedResult, serializer.serialize(items));
	}

}