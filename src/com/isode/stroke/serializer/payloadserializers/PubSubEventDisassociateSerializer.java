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
import com.isode.stroke.elements.PubSubEventDisassociate;

public class PubSubEventDisassociateSerializer extends GenericPayloadSerializer<PubSubEventDisassociate> {
public PubSubEventDisassociateSerializer(PayloadSerializerCollection serializers) {
	super(PubSubEventDisassociate.class);

	serializers_ = serializers;
}

protected String serializePayload(PubSubEventDisassociate payload) {
	if (payload == null) {
		return "";
	}

	XMLElement element = new XMLElement("disassociate", "http://jabber.org/protocol/pubsub#event");

	if(payload.getNode() != null) {
		element.setAttribute("node", payload.getNode());
	}

	return element.serialize();
}

PayloadSerializerCollection serializers_;
}
