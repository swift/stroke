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
import com.isode.stroke.elements.PubSubSubscription;
import com.isode.stroke.serializer.xml.XMLRawTextNode;
import com.isode.stroke.elements.PubSubSubscriptions;

public class PubSubSubscriptionsSerializer extends GenericPayloadSerializer<PubSubSubscriptions> {
public PubSubSubscriptionsSerializer(PayloadSerializerCollection serializers) {
	super(PubSubSubscriptions.class);

	serializers_ = serializers;
}

protected String serializePayload(PubSubSubscriptions payload) {
	if (payload == null) {
		return "";
	}

	XMLElement element = new XMLElement("subscriptions", "http://jabber.org/protocol/pubsub");

	for (PubSubSubscription item : payload.getSubscriptions()) {
		element.addNode(new XMLRawTextNode((new PubSubSubscriptionSerializer(serializers_)).serialize(item)));
	}

	if(payload.getNode() != null) {
		element.setAttribute("node", payload.getNode());
	}

	return element.serialize();
}

PayloadSerializerCollection serializers_;
}
