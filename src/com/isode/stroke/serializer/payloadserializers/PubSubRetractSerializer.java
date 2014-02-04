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
import com.isode.stroke.elements.PubSubRetract;

public class PubSubRetractSerializer extends GenericPayloadSerializer<PubSubRetract> {
public PubSubRetractSerializer(PayloadSerializerCollection serializers) {
	super(PubSubRetract.class);

	serializers_ = serializers;
}

protected String serializePayload(PubSubRetract payload) {
	if (payload == null) {
		return "";
	}

	XMLElement element = new XMLElement("retract", "http://jabber.org/protocol/pubsub");

	for (PubSubItem item : payload.getItems()) {
		element.addNode(new XMLRawTextNode((new PubSubItemSerializer(serializers_)).serialize(item)));
	}

	if(payload.getNode() != null) {
		element.setAttribute("node", payload.getNode());
	}

	element.setAttribute("notify", payload.isNotify() ? "true" : "false");

	return element.serialize();
}

PayloadSerializerCollection serializers_;
}
