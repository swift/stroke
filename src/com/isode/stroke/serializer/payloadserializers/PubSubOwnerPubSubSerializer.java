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

import com.isode.stroke.elements.PubSubOwnerPayload;
import com.isode.stroke.elements.PubSubOwnerPubSub;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.PayloadSerializer;
import com.isode.stroke.serializer.PayloadSerializerCollection;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.serializer.xml.XMLRawTextNode;

public class PubSubOwnerPubSubSerializer extends GenericPayloadSerializer<PubSubOwnerPubSub> {

public PubSubOwnerPubSubSerializer(PayloadSerializerCollection serializers)
{
	super(PubSubOwnerPubSub.class);
	
	serializers_ = serializers;
	
	pubsubSerializers_.add(new PubSubOwnerConfigureSerializer(serializers));
	pubsubSerializers_.add(new PubSubOwnerSubscriptionsSerializer(serializers));
	pubsubSerializers_.add(new PubSubOwnerDefaultSerializer(serializers));
	pubsubSerializers_.add(new PubSubOwnerPurgeSerializer(serializers));
	pubsubSerializers_.add(new PubSubOwnerAffiliationsSerializer(serializers));
	pubsubSerializers_.add(new PubSubOwnerDeleteSerializer(serializers));
}

protected String serializePayload(PubSubOwnerPubSub payload)
{
	if (payload == null) {
		return "";
	}
	XMLElement element = new XMLElement("pubsub", "http://jabber.org/protocol/pubsub#owner");
	PubSubOwnerPayload p = payload.getPayload();
	for (PayloadSerializer serializer : pubsubSerializers_) {
		if (serializer.canSerialize(p)) {
			element.addNode(new XMLRawTextNode(serializer.serialize(p)));
		}
	}
	return element.serialize();
}

PayloadSerializerCollection serializers_;
ArrayList<PayloadSerializer> pubsubSerializers_ = new ArrayList<PayloadSerializer>();

}
