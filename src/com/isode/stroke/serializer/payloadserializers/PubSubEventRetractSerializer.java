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
import com.isode.stroke.elements.PubSubEventRetract;

public class PubSubEventRetractSerializer extends GenericPayloadSerializer<PubSubEventRetract> {
public PubSubEventRetractSerializer(PayloadSerializerCollection serializers) {
	super(PubSubEventRetract.class);

	serializers_ = serializers;
}

protected String serializePayload(PubSubEventRetract payload) {
	if (payload == null) {
		return "";
	}

	XMLElement element = new XMLElement("retract", "http://jabber.org/protocol/pubsub#event");

	if(payload.getID() != null) {
		element.setAttribute("id", payload.getID());
	}

	return element.serialize();
}

PayloadSerializerCollection serializers_;
}
