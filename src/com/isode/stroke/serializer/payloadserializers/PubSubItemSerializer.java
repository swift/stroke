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
import com.isode.stroke.elements.Payload;
import com.isode.stroke.serializer.xml.XMLRawTextNode;
import com.isode.stroke.elements.PubSubItem;

public class PubSubItemSerializer extends GenericPayloadSerializer<PubSubItem> {
public PubSubItemSerializer(PayloadSerializerCollection serializers) {
	super(PubSubItem.class);

	serializers_ = serializers;
}

protected String serializePayload(PubSubItem payload) {
	if (payload == null) {
		return "";
	}

	XMLElement element = new XMLElement("item", "http://jabber.org/protocol/pubsub");

	for (Payload item : payload.getData()) {
		element.addNode(new XMLRawTextNode(serializers_.getPayloadSerializer(item).serialize(item)));
	}

	if(payload.getID() != null) {
		element.setAttribute("id", payload.getID());
	}

	return element.serialize();
}

PayloadSerializerCollection serializers_;
}
