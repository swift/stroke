/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.DiscoInfo;
import com.isode.stroke.elements.Form;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.serializer.xml.XMLRawTextNode;

class DiscoInfoSerializer extends GenericPayloadSerializer<DiscoInfo>{

    public DiscoInfoSerializer() {
        super(DiscoInfo.class);
    }

    @Override
    protected String serializePayload(DiscoInfo discoInfo) {
        XMLElement queryElement = new XMLElement("query", "http://jabber.org/protocol/disco#info");
	if (!discoInfo.getNode().isEmpty()) {
		queryElement.setAttribute("node", discoInfo.getNode());
	}
	for (DiscoInfo.Identity identity : discoInfo.getIdentities()) {
		XMLElement identityElement = new XMLElement("identity");
		if (!identity.getLanguage().isEmpty()) {
			identityElement.setAttribute("xml:lang", identity.getLanguage());
		}
		identityElement.setAttribute("category", identity.getCategory());
		identityElement.setAttribute("name", identity.getName());
		identityElement.setAttribute("type", identity.getType());
		queryElement.addNode(identityElement);
	}
	for (String feature : discoInfo.getFeatures()) {
		XMLElement featureElement = new XMLElement("feature");
		featureElement.setAttribute("var", feature);
		queryElement.addNode(featureElement);
	}
	for (Form extension : discoInfo.getExtensions()) {
		queryElement.addNode(new XMLRawTextNode(new FormSerializer().serialize(extension)));
	}
	return queryElement.serialize();
    }

}
