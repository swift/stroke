/*
* Copyright (c) 2014 Kevin Smith and Remko Tronçon
* All rights reserved.
*/

/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/

package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.MAMArchived;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLElement;

public class MAMArchivedSerializer extends GenericPayloadSerializer<MAMArchived> {
    public MAMArchivedSerializer() {
        super(MAMArchived.class);
    }

    protected String serializePayload(MAMArchived payload) {
        if (payload == null) {
            return "";
        }

        XMLElement element = new XMLElement("archived", "urn:xmpp:mam:0");
        element.setAttribute("by", payload.getBy().toString());
        element.setAttribute("id", payload.getID());
    
        return element.serialize();
    }
}
