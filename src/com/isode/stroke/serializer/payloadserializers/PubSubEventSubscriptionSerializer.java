/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/
/*
* Copyright (c) 2014, Remko Tronçon.
* All rights reserved.
*/

package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.base.DateTime;
import com.isode.stroke.elements.PubSubEventSubscription;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.PayloadSerializerCollection;
import com.isode.stroke.serializer.xml.XMLElement;

public class PubSubEventSubscriptionSerializer extends GenericPayloadSerializer<PubSubEventSubscription> {
public PubSubEventSubscriptionSerializer(PayloadSerializerCollection serializers) {
	super(PubSubEventSubscription.class);

	serializers_ = serializers;
}

protected String serializePayload(PubSubEventSubscription payload) {
	if (payload == null) {
		return "";
	}

	XMLElement element = new XMLElement("subscription", "http://jabber.org/protocol/pubsub#event");

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

	if(payload.getExpiry() != null) {
		element.setAttribute("expiry", DateTime.dateToString(payload.getExpiry()));
	}

	return element.serialize();
}

private static String serializeSubscriptionType(PubSubEventSubscription.SubscriptionType value) {
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
