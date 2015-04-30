/*
 * Copyright (c) 2010-2015 Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.SecurityLabel;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.serializer.xml.XMLRawTextNode;
import com.isode.stroke.serializer.xml.XMLTextNode;

public class SecurityLabelSerializer extends GenericPayloadSerializer<SecurityLabel> {

    public SecurityLabelSerializer() {
        super(SecurityLabel.class);
    }

    @Override
    protected String serializePayload(SecurityLabel label) {
        XMLElement element = new XMLElement("securitylabel", "urn:xmpp:sec-label:0");
        if (!label.getDisplayMarking().isEmpty()) {
            XMLElement displayMarking = new XMLElement("displaymarking");
            if (!label.getForegroundColor().isEmpty()) {
                displayMarking.setAttribute("fgcolor", label.getForegroundColor());
            }
            if (!label.getBackgroundColor().isEmpty()) {
                displayMarking.setAttribute("bgcolor", label.getBackgroundColor());
            }
            displayMarking.addNode(new XMLTextNode(label.getDisplayMarking()));
            element.addNode(displayMarking);
        }

        XMLElement labelElement = new XMLElement("label");
        labelElement.addNode(new XMLRawTextNode(label.getLabel()));
        element.addNode(labelElement);
        
        for(String equivalentLabel : label.getEquivalentLabels()) {
            XMLElement equivalentLabelElement = new XMLElement("equivalentlabel");
            equivalentLabelElement.addNode(new XMLRawTextNode(equivalentLabel));
            element.addNode(equivalentLabelElement);
        }
        return element.serialize();
    }
}
