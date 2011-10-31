/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.ResourceBind;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.serializer.xml.XMLTextNode;

class ResourceBindSerializer extends GenericPayloadSerializer<ResourceBind> {

    public ResourceBindSerializer() {
        super(ResourceBind.class);
    }

    @Override
    protected String serializePayload(ResourceBind resourceBind) {
        XMLElement bindElement = new XMLElement("bind", "urn:ietf:params:xml:ns:xmpp-bind");
	if (resourceBind.getJID().isValid()) {
		XMLElement jidNode = new XMLElement("jid");
		jidNode.addNode(new XMLTextNode(resourceBind.getJID().toString()));
		bindElement.addNode(jidNode);
	}
	else if (resourceBind.getResource().length() != 0) {
		XMLElement resourceNode = new XMLElement("resource");
		resourceNode.addNode(new XMLTextNode(resourceBind.getResource()));
		bindElement.addNode(resourceNode);
	}
	return bindElement.serialize();
    }

}
