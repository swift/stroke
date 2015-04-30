/*
 * Copyright (c) 2010-2015 Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.SecurityLabelsCatalog;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.serializer.xml.XMLRawTextNode;

public class SecurityLabelsCatalogSerializer extends GenericPayloadSerializer<SecurityLabelsCatalog> {

    public SecurityLabelsCatalogSerializer() {
        super(SecurityLabelsCatalog.class);
    }

    @Override
    protected String serializePayload(SecurityLabelsCatalog catalog) {
        XMLElement element = new XMLElement("catalog", "urn:xmpp:sec-label:catalog:2");
        if (!catalog.getName().isEmpty()) {
            element.setAttribute("name", catalog.getName());
        }
        if (catalog.getTo().isValid()) {
            element.setAttribute("to", catalog.getTo().toString());
        }
        if (!catalog.getDescription().isEmpty()) {
            element.setAttribute("desc", catalog.getDescription());
        }
        for (SecurityLabelsCatalog.Item item : catalog.getItems()) {
            XMLElement itemElement = new XMLElement("item");
            itemElement.setAttribute("selector", item.getSelector());
            if (item.getIsDefault()) {
                itemElement.setAttribute("default", "true");
            }
            if (item.getLabel() != null) {
                String serializedLabel = new SecurityLabelSerializer().serialize(item.getLabel());
                itemElement.addNode(new XMLRawTextNode(serializedLabel));
            }
            element.addNode(itemElement);
        }
        return element.serialize();
    }
}
