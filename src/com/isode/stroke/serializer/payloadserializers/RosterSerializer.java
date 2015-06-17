/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.RosterItemPayload;
import com.isode.stroke.elements.RosterPayload;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.serializer.xml.XMLTextNode;
import com.isode.stroke.serializer.xml.XMLRawTextNode;

/**
 * Roster to string.
 */
public class RosterSerializer extends GenericPayloadSerializer<RosterPayload> {

    public RosterSerializer() {
        super(RosterPayload.class);
    }

    @Override
    protected String serializePayload(RosterPayload roster) {
        XMLElement queryElement = new XMLElement("query", "jabber:iq:roster");
       	if (roster.getVersion() != null) {
			queryElement.setAttribute("ver", roster.getVersion());
		}
	for (RosterItemPayload item : roster.getItems()) {
		XMLElement itemElement = new XMLElement("item");
		itemElement.setAttribute("jid", item.getJID().toString());
                if (item.getName() != null) {
			itemElement.setAttribute("name", item.getName());
		}

		if (item.getSubscription() != null) {
			switch (item.getSubscription()) {
				case To: itemElement.setAttribute("subscription", "to"); break;
				case From: itemElement.setAttribute("subscription", "from"); break;
				case Both: itemElement.setAttribute("subscription", "both"); break;
				case Remove: itemElement.setAttribute("subscription", "remove"); break;
				case None: itemElement.setAttribute("subscription", "none"); break;
			}
		}

		if (item.getSubscriptionRequested()) {
			itemElement.setAttribute("ask", "subscribe");
		}

		for (String group : item.getGroups()) {
			XMLElement groupElement = new XMLElement("group");
			groupElement.addNode(new XMLTextNode(group));
			itemElement.addNode(groupElement);
		}


		if (item.getUnknownContent().length() != 0) {
			itemElement.addNode(new XMLRawTextNode(item.getUnknownContent()));
		}

		queryElement.addNode(itemElement);
	}

	return queryElement.serialize();



    }
}
