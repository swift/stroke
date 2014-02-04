/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/
/*
* Copyright (c) 2014, Remko Tronçon.
* All rights reserved.
*/

package com.isode.stroke.serializer.payloadserializers;

import java.util.ArrayList;

import com.isode.stroke.elements.PubSub;
import com.isode.stroke.elements.PubSubCreate;
import com.isode.stroke.elements.PubSubPayload;
import com.isode.stroke.elements.PubSubSubscribe;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.PayloadSerializer;
import com.isode.stroke.serializer.PayloadSerializerCollection;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.serializer.xml.XMLRawTextNode;

public class PubSubSerializer extends GenericPayloadSerializer<PubSub> {

public PubSubSerializer(PayloadSerializerCollection serializers) {
	super(PubSub.class);

	serializers_ = serializers;
	
	pubsubSerializers_.add(new PubSubItemsSerializer(serializers));
	pubsubSerializers_.add(new PubSubCreateSerializer(serializers));
	pubsubSerializers_.add(new PubSubPublishSerializer(serializers));
	pubsubSerializers_.add(new PubSubOptionsSerializer(serializers));
	pubsubSerializers_.add(new PubSubAffiliationsSerializer(serializers));
	pubsubSerializers_.add(new PubSubRetractSerializer(serializers));
	pubsubSerializers_.add(new PubSubDefaultSerializer(serializers));
	pubsubSerializers_.add(new PubSubSubscriptionsSerializer(serializers));
	pubsubSerializers_.add(new PubSubSubscribeSerializer(serializers));
	pubsubSerializers_.add(new PubSubUnsubscribeSerializer(serializers));
	pubsubSerializers_.add(new PubSubSubscriptionSerializer(serializers));
}

@Override
protected String serializePayload(PubSub payload) {
	if (payload == null) {
		return "";
	}

	XMLElement element = new XMLElement("pubsub", "http://jabber.org/protocol/pubsub");
	PubSubPayload p = payload.getPayload();
	
	for (PayloadSerializer serializer : pubsubSerializers_) {
		if (serializer.canSerialize(p)) {
			element.addNode(new XMLRawTextNode(serializer.serialize(p)));
			PubSubCreate create = null;
			if(p instanceof PubSubCreate) {
				create = (PubSubCreate)p;
				element.addNode(new XMLRawTextNode((new PubSubConfigureSerializer(serializers_)).serialize(create.getConfigure())));
			}
			PubSubSubscribe subscribe = null;
			if (p instanceof PubSubSubscribe) {
				subscribe = (PubSubSubscribe)p;
				element.addNode(new XMLRawTextNode((new PubSubConfigureSerializer(serializers_)).serialize(subscribe.getOptions())));
			}
		}
	}
	return element.serialize();
}

ArrayList<PayloadSerializer> pubsubSerializers_ = new ArrayList<PayloadSerializer>();
PayloadSerializerCollection serializers_;
}
