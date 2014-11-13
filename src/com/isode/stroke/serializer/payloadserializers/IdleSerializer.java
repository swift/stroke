/*
 * Copyright (c) 2015 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2013 Tobias Markmann
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.base.DateTime;
import com.isode.stroke.elements.Idle;
import com.isode.stroke.serializer.GenericPayloadSerializer;

public class IdleSerializer extends GenericPayloadSerializer<Idle> {

    public IdleSerializer() {
        super(Idle.class);
    }

    @Override
    protected String serializePayload(Idle idle) {
        return "<idle xmlns='urn:xmpp:idle:1' since='" + DateTime.dateToString(idle.getSince()) + "'/>";
    }
}
