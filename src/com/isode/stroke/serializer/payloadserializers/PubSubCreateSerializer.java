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
import com.isode.stroke.elements.PubSubConfigure;
import com.isode.stroke.elements.PubSubCreate;

public class PubSubCreateSerializer extends GenericPayloadSerializer<PubSubCreate> {
public PubSubCreateSerializer(PayloadSerializerCollection serializers) {
	super(PubSubCreate.class);

	serializers_ = serializers;
}

protected String serializePayload(PubSubCreate payload) {
	if (payload == null) {
		return "";
	}

	XMLElement element = new XMLElement("create", "http://jabber.org/protocol/pubsub");

	if(payload.getNode() != null) {
		element.setAttribute("node", payload.getNode());
	}

	return element.serialize();
}

PayloadSerializerCollection serializers_;
}
