/*
* Copyright (c) 2013-2015, Isode Limited, London, England.
* All rights reserved.
*/

package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.PayloadSerializerCollection;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.serializer.xml.XMLRawTextNode;
import com.isode.stroke.elements.PubSubSubscription;

public class PubSubSubscriptionSerializer extends GenericPayloadSerializer<PubSubSubscription> {
public PubSubSubscriptionSerializer(PayloadSerializerCollection serializers) {
	super(PubSubSubscription.class);

	serializers_ = serializers;
}

protected String serializePayload(PubSubSubscription payload) {
	if (payload == null) {
		return "";
	}

	XMLElement element = new XMLElement("subscription", "http://jabber.org/protocol/pubsub");

	element.setAttribute("subscription", serializeSubscriptionType(payload.getSubscription()));

	if(payload.getSubscriptionID() != null) {
		element.setAttribute("subid", payload.getSubscriptionID());
	}

	if(payload.getNode() != null) {
		element.setAttribute("node", payload.getNode());
	}

	if(payload.getJID() != null) {
		element.setAttribute("jid", payload.getJID().toString());
	}

	element.addNode(new XMLRawTextNode((new PubSubSubscribeOptionsSerializer(serializers_)).serialize(payload.getOptions())));

	return element.serialize();
}

private static String serializeSubscriptionType(PubSubSubscription.SubscriptionType value) {
	switch (value) {
		case None: return "none";
		case Pending: return "pending";
		case Subscribed: return "subscribed";
		case Unconfigured: return "unconfigured";
	}
	return "undefined-subscriptiontype";
}

PayloadSerializerCollection serializers_;
}
