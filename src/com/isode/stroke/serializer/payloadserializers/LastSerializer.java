/*
 * Copyright (c) 2011 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2011 Kevin Smith
 * All rights reserved.
 */
package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.Last;
import com.isode.stroke.serializer.GenericPayloadSerializer;

public class LastSerializer extends GenericPayloadSerializer<Last> {

    public LastSerializer() {
        super(Last.class);
    }

    @Override
    protected String serializePayload(Last last) {
        return "<query xmlns='jabber:iq:last' seconds='" + Integer.toString(last.getSeconds()) + "'/>";
    }
}
