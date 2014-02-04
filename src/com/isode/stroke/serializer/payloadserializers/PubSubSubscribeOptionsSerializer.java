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
import com.isode.stroke.elements.PubSubSubscribeOptions;

public class PubSubSubscribeOptionsSerializer extends GenericPayloadSerializer<PubSubSubscribeOptions> {
public PubSubSubscribeOptionsSerializer(PayloadSerializerCollection serializers) {
	super(PubSubSubscribeOptions.class);

	serializers_ = serializers;
}

protected String serializePayload(PubSubSubscribeOptions payload) {
	if (payload == null) {
		return "";
	}

	XMLElement element = new XMLElement("subscribe-options", "http://jabber.org/protocol/pubsub");

	if (payload.isRequired()) {
		element.addNode(new XMLElement("required", ""));
	}

	return element.serialize();
}

PayloadSerializerCollection serializers_;
}
