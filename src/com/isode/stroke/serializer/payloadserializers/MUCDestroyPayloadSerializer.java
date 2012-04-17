/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2011, Kevin Smith
 * All rights reserved.
 */
package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.MUCDestroyPayload;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.serializer.xml.XMLTextNode;

/**
 * Serializer for {@link MUCDestroyPayload} element.
 */
public class MUCDestroyPayloadSerializer extends GenericPayloadSerializer<MUCDestroyPayload> {
    /**
     * Create the serializer 
     */
    public MUCDestroyPayloadSerializer() {
        super(MUCDestroyPayload.class);
    }

    @Override
    public String serializePayload(MUCDestroyPayload payload) {
        XMLElement mucElement = new XMLElement("destroy", "");
        if (payload.getReason() != null && !payload.getReason().isEmpty()) {
            XMLElement reason = new XMLElement("reason", "");
            reason.addNode(new XMLTextNode(payload.getReason()));
            mucElement.addNode(reason);
        }
        if (payload.getNewVenue() != null && payload.getNewVenue().isValid()) {
            mucElement.setAttribute("jid", payload.getNewVenue().toString());
        }
        return mucElement.serialize();
    }
}
