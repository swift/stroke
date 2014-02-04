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
import com.isode.stroke.elements.PubSubAffiliation;

public class PubSubAffiliationSerializer extends GenericPayloadSerializer<PubSubAffiliation> {
public PubSubAffiliationSerializer(PayloadSerializerCollection serializers) {
	super(PubSubAffiliation.class);

	serializers_ = serializers;
}

protected String serializePayload(PubSubAffiliation payload) {
	if (payload == null) {
		return "";
	}

	XMLElement element = new XMLElement("affiliation", "http://jabber.org/protocol/pubsub");

	if(payload.getNode() != null) {
		element.setAttribute("node", payload.getNode());
	}

	element.setAttribute("affiliation", serializeType(payload.getType()));

	return element.serialize();
}

private static String serializeType(PubSubAffiliation.Type value) {
	switch (value) {
		case None: return "none";
		case Member: return "member";
		case Outcast: return "outcast";
		case Owner: return "owner";
		case Publisher: return "publisher";
		case PublishOnly: return "publish-only";
	}
	return "undefined-type";
}

PayloadSerializerCollection serializers_;
}
