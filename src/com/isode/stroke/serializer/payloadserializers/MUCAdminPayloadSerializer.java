/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2011, Kevin Smith
 * All rights reserved.
 */
package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.MUCAdminPayload;
import com.isode.stroke.elements.MUCItem;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLElement;

/**
 * Serializer for {@link MUCAdminPayload} element.
 */
public final class MUCAdminPayloadSerializer extends GenericPayloadSerializer<MUCAdminPayload> {

    /**
     * Constructor 
     */
    public MUCAdminPayloadSerializer() {
        super(MUCAdminPayload.class);
    }

    @Override
    public String serializePayload(MUCAdminPayload payload) {
        XMLElement mucElement = new XMLElement("query", "http://jabber.org/protocol/muc#admin");
        for(MUCItem item : payload.getItems()) {
            mucElement.addNode(MUCItemSerializer.itemToElement(item));
        }
        return mucElement.serialize();
    }
}
