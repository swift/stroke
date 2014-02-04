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
import com.isode.stroke.elements.PubSubEventItem;
import com.isode.stroke.serializer.xml.XMLRawTextNode;
import com.isode.stroke.elements.PubSubEventRetract;
import com.isode.stroke.elements.PubSubEventItems;

public class PubSubEventItemsSerializer extends GenericPayloadSerializer<PubSubEventItems> {
public PubSubEventItemsSerializer(PayloadSerializerCollection serializers) {
	super(PubSubEventItems.class);

	serializers_ = serializers;
}

protected String serializePayload(PubSubEventItems payload) {
	if (payload == null) {
		return "";
	}

	XMLElement element = new XMLElement("items", "http://jabber.org/protocol/pubsub#event");

	for (PubSubEventItem item : payload.getItems()) {
		element.addNode(new XMLRawTextNode((new PubSubEventItemSerializer(serializers_)).serialize(item)));
	}

	if(payload.getNode() != null) {
		element.setAttribute("node", payload.getNode());
	}

	for (PubSubEventRetract item : payload.getRetracts()) {
		element.addNode(new XMLRawTextNode((new PubSubEventRetractSerializer(serializers_)).serialize(item)));
	}

	return element.serialize();
}

PayloadSerializerCollection serializers_;
}
