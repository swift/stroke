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
import com.isode.stroke.serializer.xml.XMLRawTextNode;
import com.isode.stroke.elements.PubSubOwnerDelete;

public class PubSubOwnerDeleteSerializer extends GenericPayloadSerializer<PubSubOwnerDelete> {
public PubSubOwnerDeleteSerializer(PayloadSerializerCollection serializers) {
	super(PubSubOwnerDelete.class);

	serializers_ = serializers;
}

protected String serializePayload(PubSubOwnerDelete payload) {
	if (payload == null) {
		return "";
	}

	XMLElement element = new XMLElement("delete", "http://jabber.org/protocol/pubsub#owner");

	element.addNode(new XMLRawTextNode((new PubSubOwnerRedirectSerializer(serializers_)).serialize(payload.getRedirect())));

	if(payload.getNode() != null) {
		element.setAttribute("node", payload.getNode());
	}

	return element.serialize();
}

PayloadSerializerCollection serializers_;
}
