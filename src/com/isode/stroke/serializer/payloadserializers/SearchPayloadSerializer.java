/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.SearchPayload;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.serializer.xml.XMLRawTextNode;
import com.isode.stroke.elements.Form;

/**
 * SearchPayload to String.
 */
public class SearchPayloadSerializer extends GenericPayloadSerializer<SearchPayload> {

    public SearchPayloadSerializer() {
        super(SearchPayload.class);
    }

    @Override
    protected String serializePayload(SearchPayload searchPayload) {
        XMLElement searchElement = new XMLElement("query", "jabber:iq:search");

	if (searchPayload.getInstructions() != null) {
		searchElement.addNode(new XMLElement("instructions", "", searchPayload.getInstructions()));
	}

	if (searchPayload.getNick() != null) {
		searchElement.addNode(new XMLElement("nick", "", searchPayload.getNick()));
	}

	if (searchPayload.getFirst() != null) {
		searchElement.addNode(new XMLElement("first", "", searchPayload.getFirst()));
	}

	if (searchPayload.getLast() != null) {
		searchElement.addNode(new XMLElement("last", "", searchPayload.getLast()));
	}

	if (searchPayload.getEMail() != null) {
		searchElement.addNode(new XMLElement("email", "", searchPayload.getEMail()));
	}

	for (SearchPayload.Item item : searchPayload.getItems()) {
		XMLElement itemElement = new XMLElement("item");
		itemElement.setAttribute("jid", item.jid.toString());
		itemElement.addNode(new XMLElement("first", "", item.first));
		itemElement.addNode(new XMLElement("last", "", item.last));
		itemElement.addNode(new XMLElement("nick", "", item.nick));
		itemElement.addNode(new XMLElement("email", "", item.email));

		searchElement.addNode(itemElement);
	}

	if (searchPayload.getForm() != null) {
		Form form = searchPayload.getForm();
		searchElement.addNode(new XMLRawTextNode(new FormSerializer().serialize(form)));
	}

	return searchElement.serialize();
    }
}
