/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.RawXMLPayload;
import com.isode.stroke.serializer.GenericPayloadSerializer;

class RawXMLPayloadSerializer extends GenericPayloadSerializer<RawXMLPayload> {

    public RawXMLPayloadSerializer() {
        super(RawXMLPayload.class);
    }

    @Override
    protected String serializePayload(RawXMLPayload payload) {
        return payload.getRawXML();
    }

}
