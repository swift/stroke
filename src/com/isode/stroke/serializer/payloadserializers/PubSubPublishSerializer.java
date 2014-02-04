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
import com.isode.stroke.elements.PubSubPublish;

public class PubSubPublishSerializer extends GenericPayloadSerializer<PubSubPublish> {
public PubSubPublishSerializer(PayloadSerializerCollection serializers) {
	super(PubSubPublish.class);

	serializers_ = serializers;
}

protected String serializePayload(PubSubPublish payload) {
	if (payload == null) {
		return "";
	}

	XMLElement element = new XMLElement("publish", "http://jabber.org/protocol/pubsub");

	for (PubSubItem item : payload.getItems()) {
		element.addNode(new XMLRawTextNode((new PubSubItemSerializer(serializers_)).serialize(item)));
	}

	if(payload.getNode() != null) {
		element.setAttribute("node", payload.getNode());
	}

	return element.serialize();
}

PayloadSerializerCollection serializers_;
}
