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
import com.isode.stroke.elements.PubSubOwnerRedirect;

public class PubSubOwnerRedirectSerializer extends GenericPayloadSerializer<PubSubOwnerRedirect> {
public PubSubOwnerRedirectSerializer(PayloadSerializerCollection serializers) {
	super(PubSubOwnerRedirect.class);

	serializers_ = serializers;
}

protected String serializePayload(PubSubOwnerRedirect payload) {
	if (payload == null) {
		return "";
	}

	XMLElement element = new XMLElement("redirect", "http://jabber.org/protocol/pubsub#owner");

	if(payload.getURI() != null) {
		element.setAttribute("uri", payload.getURI());
	}

	return element.serialize();
}

PayloadSerializerCollection serializers_;
}
