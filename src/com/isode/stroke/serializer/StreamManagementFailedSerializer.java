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
import com.isode.stroke.elements.StreamManagementFailed;
import com.isode.stroke.serializer.xml.XMLElement;

class StreamManagementFailedSerializer extends GenericElementSerializer<StreamManagementFailed> {

    public StreamManagementFailedSerializer() {
        super(StreamManagementFailed.class);
    }

    public String serialize(Element element) {
        return new XMLElement("failed", "urn:xmpp:sm:2").serialize();
    }

}
