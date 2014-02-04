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
import com.isode.stroke.elements.PubSubDefault;

public class PubSubDefaultSerializer extends GenericPayloadSerializer<PubSubDefault> {
public PubSubDefaultSerializer(PayloadSerializerCollection serializers) {
	super(PubSubDefault.class);

	serializers_ = serializers;
}

protected String serializePayload(PubSubDefault payload) {
	if (payload == null) {
		return "";
	}

	XMLElement element = new XMLElement("default", "http://jabber.org/protocol/pubsub");

	if(payload.getNode() != null) {
		element.setAttribute("node", payload.getNode());
	}

	element.setAttribute("type", serializeType(payload.getType()));

	return element.serialize();
}

private static String serializeType(PubSubDefault.Type value) {
	switch (value) {
		case None: return "none";
		case Collection: return "collection";
		case Leaf: return "leaf";
	}
	return "undefined-type";
}

PayloadSerializerCollection serializers_;
}
