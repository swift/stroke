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
import com.isode.stroke.elements.PubSubOptions;

public class PubSubOptionsSerializer extends GenericPayloadSerializer<PubSubOptions> {
public PubSubOptionsSerializer(PayloadSerializerCollection serializers) {
	super(PubSubOptions.class);

	serializers_ = serializers;
}

protected String serializePayload(PubSubOptions payload) {
	if (payload == null) {
		return "";
	}

	XMLElement element = new XMLElement("options", "http://jabber.org/protocol/pubsub");

	element.addNode(new XMLRawTextNode((new FormSerializer()).serialize(payload.getData())));

	if(payload.getSubscriptionID() != null) {
		element.setAttribute("subid", payload.getSubscriptionID());
	}

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
