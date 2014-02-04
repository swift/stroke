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
import com.isode.stroke.elements.PubSubConfigure;

public class PubSubConfigureSerializer extends GenericPayloadSerializer<PubSubConfigure> {
public PubSubConfigureSerializer(PayloadSerializerCollection serializers) {
	super(PubSubConfigure.class);

	serializers_ = serializers;
}

protected String serializePayload(PubSubConfigure payload) {
	if (payload == null) {
		return "";
	}

	XMLElement element = new XMLElement("configure", "http://jabber.org/protocol/pubsub");

	element.addNode(new XMLRawTextNode((new FormSerializer()).serialize(payload.getData())));

	return element.serialize();
}

PayloadSerializerCollection serializers_;
}
