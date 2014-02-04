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
import com.isode.stroke.serializer.xml.XMLRawTextNode;
import com.isode.stroke.elements.PubSubAffiliations;

public class PubSubAffiliationsSerializer extends GenericPayloadSerializer<PubSubAffiliations> {
public PubSubAffiliationsSerializer(PayloadSerializerCollection serializers) {
	super(PubSubAffiliations.class);

	serializers_ = serializers;
}

protected String serializePayload(PubSubAffiliations payload) {
	if (payload == null) {
		return "";
	}

	XMLElement element = new XMLElement("affiliations", "http://jabber.org/protocol/pubsub");

	if(payload.getNode() != null) {
		element.setAttribute("node", payload.getNode());
	}

	for (PubSubAffiliation item : payload.getAffiliations()) {
		element.addNode(new XMLRawTextNode((new PubSubAffiliationSerializer(serializers_)).serialize(item)));
	}

	return element.serialize();
}

PayloadSerializerCollection serializers_;
}
