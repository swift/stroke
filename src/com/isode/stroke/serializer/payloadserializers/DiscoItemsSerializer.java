/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Kevin Smith.
 * All rights reserved.
 */

package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.DiscoItems;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLElement;

class DiscoItemsSerializer extends GenericPayloadSerializer<DiscoItems> {

    public DiscoItemsSerializer() {
        super(DiscoItems.class);
    }

    @Override
    protected String serializePayload(DiscoItems discoItems) {
        XMLElement queryElement = new XMLElement("query", "http://jabber.org/protocol/disco#items");
	if (!discoItems.getNode().isEmpty()) {
		queryElement.setAttribute("node", discoItems.getNode());
	}
	for (DiscoItems.Item item : discoItems.getItems()) {
		XMLElement itemElement = new XMLElement("item");
		itemElement.setAttribute("name", item.getName());
		itemElement.setAttribute("jid", item.getJID().toString());
		if (!item.getNode().isEmpty()) {
			itemElement.setAttribute("node", item.getNode());
		}
		queryElement.addNode(itemElement);
	}
	return queryElement.serialize();
    }

}
