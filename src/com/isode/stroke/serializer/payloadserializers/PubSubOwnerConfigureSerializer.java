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
import com.isode.stroke.elements.PubSubOwnerConfigure;

public class PubSubOwnerConfigureSerializer extends GenericPayloadSerializer<PubSubOwnerConfigure> {
public PubSubOwnerConfigureSerializer(PayloadSerializerCollection serializers) {
	super(PubSubOwnerConfigure.class);

	serializers_ = serializers;
}

protected String serializePayload(PubSubOwnerConfigure payload) {
	if (payload == null) {
		return "";
	}

	XMLElement element = new XMLElement("configure", "http://jabber.org/protocol/pubsub#owner");

	element.addNode(new XMLRawTextNode((new FormSerializer()).serialize(payload.getData())));

	if(payload.getNode() != null) {
		element.setAttribute("node", payload.getNode());
	}

	return element.serialize();
}

PayloadSerializerCollection serializers_;
}
