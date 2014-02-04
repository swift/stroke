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
import com.isode.stroke.elements.PubSubOwnerAffiliation;
import com.isode.stroke.serializer.xml.XMLRawTextNode;
import com.isode.stroke.elements.PubSubOwnerAffiliations;

public class PubSubOwnerAffiliationsSerializer extends GenericPayloadSerializer<PubSubOwnerAffiliations> {
public PubSubOwnerAffiliationsSerializer(PayloadSerializerCollection serializers) {
	super(PubSubOwnerAffiliations.class);

	serializers_ = serializers;
}

protected String serializePayload(PubSubOwnerAffiliations payload) {
	if (payload == null) {
		return "";
	}

	XMLElement element = new XMLElement("affiliations", "http://jabber.org/protocol/pubsub#owner");

	if(payload.getNode() != null) {
		element.setAttribute("node", payload.getNode());
	}

	for (PubSubOwnerAffiliation item : payload.getAffiliations()) {
		element.addNode(new XMLRawTextNode((new PubSubOwnerAffiliationSerializer(serializers_)).serialize(item)));
	}

	return element.serialize();
}

PayloadSerializerCollection serializers_;
}
