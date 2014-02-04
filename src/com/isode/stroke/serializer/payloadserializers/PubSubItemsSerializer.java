/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/
/*
* Copyright (c) 2014, Remko Tronçon.
* All rights reserved.
*/

package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.PayloadSerializerCollection;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.elements.PubSubItem;
import com.isode.stroke.serializer.xml.XMLRawTextNode;
import com.isode.stroke.elements.PubSubItems;

public class PubSubItemsSerializer extends GenericPayloadSerializer<PubSubItems> {
public PubSubItemsSerializer(PayloadSerializerCollection serializers) {
	super(PubSubItems.class);

	serializers_ = serializers;
}

protected String serializePayload(PubSubItems payload) {
	if (payload == null) {
		return "";
	}

	XMLElement element = new XMLElement("items", "http://jabber.org/protocol/pubsub");

	if(payload.getMaximumItems() != null) {
		element.setAttribute("max_items", payload.getMaximumItems().toString());
	}

	for (PubSubItem item : payload.getItems()) {
		element.addNode(new XMLRawTextNode((new PubSubItemSerializer(serializers_)).serialize(item)));
	}

	if(payload.getSubscriptionID() != null) {
		element.setAttribute("subid", payload.getSubscriptionID());
	}

	if(payload.getNode() != null) {
		element.setAttribute("node", payload.getNode());
	}

	return element.serialize();
}

PayloadSerializerCollection serializers_;
}
