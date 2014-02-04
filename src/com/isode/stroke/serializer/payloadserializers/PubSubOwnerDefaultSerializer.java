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
import com.isode.stroke.serializer.payloadserializers.FormSerializer;
import com.isode.stroke.serializer.xml.XMLRawTextNode;
import com.isode.stroke.elements.PubSubOwnerDefault;

public class PubSubOwnerDefaultSerializer extends GenericPayloadSerializer<PubSubOwnerDefault> {
public PubSubOwnerDefaultSerializer(PayloadSerializerCollection serializers) {
	super(PubSubOwnerDefault.class);

	serializers_ = serializers;
}

protected String serializePayload(PubSubOwnerDefault payload) {
	if (payload == null) {
		return "";
	}

	XMLElement element = new XMLElement("default", "http://jabber.org/protocol/pubsub#owner");

	element.addNode(new XMLRawTextNode((new FormSerializer()).serialize(payload.getData())));

	return element.serialize();
}

PayloadSerializerCollection serializers_;
}
