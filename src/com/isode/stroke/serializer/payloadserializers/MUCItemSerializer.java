/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2011, Kevin Smith
 * All rights reserved.
 */
package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.MUCItem;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.serializer.xml.XMLTextNode;

/**
 * Class representing a serializer for {@link MUCItem}
 *
 */
public class MUCItemSerializer {

    /**
     * Create an XMLElement from the given MUC Item
     * @param item MUC Item, not null
     * @return XML Element, not null
     */
    public static XMLElement itemToElement(MUCItem item) {
        XMLElement itemElement = new XMLElement("item");
        if (item.affiliation != null) {
            itemElement.setAttribute("affiliation", item.affiliation.nodeName);
        }
        if (item.role != null) {
            itemElement.setAttribute("role", item.role.nodeName);
        }
        if (item.realJID != null) {
            itemElement.setAttribute("jid", item.realJID.toString());
        }
        if (item.nick != null) {
            itemElement.setAttribute("nick", item.nick);
        }
        if (item.actor != null) {
            XMLElement actorElement = new XMLElement("actor");
            actorElement.setAttribute("jid", item.actor.toString());
            itemElement.addNode(actorElement);
        }
        if (item.reason != null) {
            XMLElement reasonElement = new XMLElement("reason");
            reasonElement.addNode(new XMLTextNode(item.reason));
            itemElement.addNode(reasonElement);
        }
        return itemElement;
    }
}