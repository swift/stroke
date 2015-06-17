/*
 * Copyright (c) 2011 Jan Kaluza
 * Licensed under the Simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLTextNode;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.elements.RosterItemExchangePayload;
import com.isode.stroke.base.NotNull;

public class RosterItemExchangeSerializer extends GenericPayloadSerializer<RosterItemExchangePayload> {

	public RosterItemExchangeSerializer() {
		super(RosterItemExchangePayload.class);
	}

	public String serializePayload(RosterItemExchangePayload roster) {
		XMLElement queryElement = new XMLElement("x", "http://jabber.org/protocol/rosterx");
		for(RosterItemExchangePayload.Item item : roster.getItems()) {
			XMLElement itemElement = new XMLElement("item");
			itemElement.setAttribute("jid", item.getJID().toString());
			itemElement.setAttribute("name", item.getName());

			switch (item.getAction()) {
				case Add: itemElement.setAttribute("action", "add"); break;
				case Modify: itemElement.setAttribute("action", "modify"); break;
				case Delete: itemElement.setAttribute("action", "delete"); break;
			}

			for(String group : item.getGroups()) {
				XMLElement groupElement = new XMLElement("group");
				groupElement.addNode(new XMLTextNode(group));
				itemElement.addNode(groupElement);
			}

			queryElement.addNode(itemElement);
		}

		return queryElement.serialize();
	}
}