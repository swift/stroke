/*
 * Copyright (c) 2011, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.serializer;

import com.isode.stroke.elements.Element;
import com.isode.stroke.elements.StreamManagementEnabled;
import com.isode.stroke.serializer.xml.XMLElement;

class StreamManagementEnabledSerializer extends GenericElementSerializer<StreamManagementEnabled> {

    public StreamManagementEnabledSerializer() {
        super(StreamManagementEnabled.class);
    }

    public String serialize(Element el) {
        StreamManagementEnabled e = (StreamManagementEnabled) el;
        XMLElement element = new XMLElement("enabled", "urn:xmpp:sm:2");
        if (!e.getResumeID().isEmpty()) {
            element.setAttribute("id", e.getResumeID());
        }
        if (e.getResumeSupported()) {
            element.setAttribute("resume", "true");
        }
        return element.serialize();
    }
}
