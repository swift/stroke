/*
* Copyright (c) 2013-2015, Isode Limited, London, England.
* All rights reserved.
*/

package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.PayloadSerializerCollection;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.elements.PubSubSubscribe;

public class PubSubSubscribeSerializer extends GenericPayloadSerializer<PubSubSubscribe> {
public PubSubSubscribeSerializer(PayloadSerializerCollection serializers) {
	super(PubSubSubscribe.class);

	serializers_ = serializers;
}

protected String serializePayload(PubSubSubscribe payload) {
	if (payload == null) {
		return "";
	}

	XMLElement element = new XMLElement("subscribe", "http://jabber.org/protocol/pubsub");

	if(payload.getNode() != null) {
		element.setAttribute("node", payload.getNode());
	}

	if(payload.getJID() != null) {
		element.setAttribute("jid", payload.getJID().toString());
	}

	return element.serialize();
}

PayloadSerializerCollection serializers_;
}
