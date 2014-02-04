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
import com.isode.stroke.elements.PubSubOwnerPurge;

public class PubSubOwnerPurgeSerializer extends GenericPayloadSerializer<PubSubOwnerPurge> {
public PubSubOwnerPurgeSerializer(PayloadSerializerCollection serializers) {
	super(PubSubOwnerPurge.class);

	serializers_ = serializers;
}

protected String serializePayload(PubSubOwnerPurge payload) {
	if (payload == null) {
		return "";
	}

	XMLElement element = new XMLElement("purge", "http://jabber.org/protocol/pubsub#owner");

	if(payload.getNode() != null) {
		element.setAttribute("node", payload.getNode());
	}

	return element.serialize();
}

PayloadSerializerCollection serializers_;
}
