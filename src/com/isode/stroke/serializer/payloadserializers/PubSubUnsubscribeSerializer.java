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
import com.isode.stroke.elements.PubSubUnsubscribe;

public class PubSubUnsubscribeSerializer extends GenericPayloadSerializer<PubSubUnsubscribe> {
public PubSubUnsubscribeSerializer(PayloadSerializerCollection serializers) {
	super(PubSubUnsubscribe.class);

	serializers_ = serializers;
}

protected String serializePayload(PubSubUnsubscribe payload) {
	if (payload == null) {
		return "";
	}

	XMLElement element = new XMLElement("unsubscribe", "http://jabber.org/protocol/pubsub");

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
