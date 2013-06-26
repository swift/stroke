/*
 * Copyright (c) 2011-2013 Isode Limited, London, England.
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
        if(last.getSeconds() == null) {
            return  "<query xmlns='jabber:iq:last'/>";
        }
        return "<query xmlns='jabber:iq:last' seconds='" + Long.toString(last.getSeconds()) + "'/>";
    }
}
