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
import com.isode.stroke.serializer.xml.XMLRawTextNode;
import com.isode.stroke.elements.PubSubOwnerSubscriptions;

public class PubSubOwnerSubscriptionsSerializer extends GenericPayloadSerializer<PubSubOwnerSubscriptions> {
public PubSubOwnerSubscriptionsSerializer(PayloadSerializerCollection serializers) {
	super(PubSubOwnerSubscriptions.class);

	serializers_ = serializers;
}

protected String serializePayload(PubSubOwnerSubscriptions payload) {
	if (payload == null) {
		return "";
	}

	XMLElement element = new XMLElement("subscriptions", "http://jabber.org/protocol/pubsub#owner");

	for (PubSubOwnerSubscription item : payload.getSubscriptions()) {
		element.addNode(new XMLRawTextNode((new PubSubOwnerSubscriptionSerializer(serializers_)).serialize(item)));
	}

	if(payload.getNode() != null) {
		element.setAttribute("node", payload.getNode());
	}

	return element.serialize();
}

PayloadSerializerCollection serializers_;
}
