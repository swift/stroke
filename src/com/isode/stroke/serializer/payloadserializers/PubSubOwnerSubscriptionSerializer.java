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
import com.isode.stroke.elements.PubSubOwnerSubscription;

public class PubSubOwnerSubscriptionSerializer extends GenericPayloadSerializer<PubSubOwnerSubscription> {
public PubSubOwnerSubscriptionSerializer(PayloadSerializerCollection serializers) {
	super(PubSubOwnerSubscription.class);

	serializers_ = serializers;
}

protected String serializePayload(PubSubOwnerSubscription payload) {
	if (payload == null) {
		return "";
	}

	XMLElement element = new XMLElement("subscription", "http://jabber.org/protocol/pubsub#owner");

	element.setAttribute("subscription", serializeSubscriptionType(payload.getSubscription()));

	if(payload.getJID() != null) {
		element.setAttribute("jid", payload.getJID().toString());
	}

	return element.serialize();
}

private static String serializeSubscriptionType(PubSubOwnerSubscription.SubscriptionType value) {
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
