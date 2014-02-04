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
import com.isode.stroke.elements.PubSubEventRedirect;

public class PubSubEventRedirectSerializer extends GenericPayloadSerializer<PubSubEventRedirect> {
public PubSubEventRedirectSerializer(PayloadSerializerCollection serializers) {
	super(PubSubEventRedirect.class);

	serializers_ = serializers;
}

protected String serializePayload(PubSubEventRedirect payload) {
	if (payload == null) {
		return "";
	}

	XMLElement element = new XMLElement("redirect", "http://jabber.org/protocol/pubsub#event");

	if(payload.getURI() != null) {
		element.setAttribute("uri", payload.getURI());
	}

	return element.serialize();
}

PayloadSerializerCollection serializers_;
}
