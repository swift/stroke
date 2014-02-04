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
import com.isode.stroke.serializer.xml.XMLRawTextNode;
import com.isode.stroke.elements.PubSubEventDelete;

public class PubSubEventDeleteSerializer extends GenericPayloadSerializer<PubSubEventDelete> {
public PubSubEventDeleteSerializer(PayloadSerializerCollection serializers) {
	super(PubSubEventDelete.class);

	serializers_ = serializers;
}

protected String serializePayload(PubSubEventDelete payload) {
	if (payload == null) {
		return "";
	}

	XMLElement element = new XMLElement("delete", "http://jabber.org/protocol/pubsub#event");

	element.addNode(new XMLRawTextNode((new PubSubEventRedirectSerializer(serializers_)).serialize(payload.getRedirects())));

	if(payload.getNode() != null) {
		element.setAttribute("node", payload.getNode());
	}

	return element.serialize();
}

PayloadSerializerCollection serializers_;
}
