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
import com.isode.stroke.elements.PubSubEventAssociate;
import com.isode.stroke.serializer.xml.XMLRawTextNode;
import com.isode.stroke.elements.PubSubEventDisassociate;
import com.isode.stroke.elements.PubSubEventCollection;

public class PubSubEventCollectionSerializer extends GenericPayloadSerializer<PubSubEventCollection> {
public PubSubEventCollectionSerializer(PayloadSerializerCollection serializers) {
	super(PubSubEventCollection.class);

	serializers_ = serializers;
}

protected String serializePayload(PubSubEventCollection payload) {
	if (payload == null) {
		return "";
	}

	XMLElement element = new XMLElement("collection", "http://jabber.org/protocol/pubsub#event");

	element.addNode(new XMLRawTextNode((new PubSubEventAssociateSerializer(serializers_)).serialize(payload.getAssociate())));

	if(payload.getNode() != null) {
		element.setAttribute("node", payload.getNode());
	}

	element.addNode(new XMLRawTextNode((new PubSubEventDisassociateSerializer(serializers_)).serialize(payload.getDisassociate())));

	return element.serialize();
}

PayloadSerializerCollection serializers_;
}
