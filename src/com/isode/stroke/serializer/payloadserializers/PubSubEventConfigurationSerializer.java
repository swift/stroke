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
import com.isode.stroke.serializer.payloadserializers.FormSerializer;
import com.isode.stroke.serializer.xml.XMLRawTextNode;
import com.isode.stroke.elements.PubSubEventConfiguration;

public class PubSubEventConfigurationSerializer extends GenericPayloadSerializer<PubSubEventConfiguration> {
public PubSubEventConfigurationSerializer(PayloadSerializerCollection serializers) {
	super(PubSubEventConfiguration.class);

	serializers_ = serializers;
}

protected String serializePayload(PubSubEventConfiguration payload) {
	if (payload == null) {
		return "";
	}

	XMLElement element = new XMLElement("configuration", "http://jabber.org/protocol/pubsub#event");

	element.addNode(new XMLRawTextNode((new FormSerializer()).serialize(payload.getData())));

	if(payload.getNode() != null) {
		element.setAttribute("node", payload.getNode());
	}

	return element.serialize();
}

PayloadSerializerCollection serializers_;
}
