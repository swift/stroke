/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.StartSession;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLElement;

class StartSessionSerializer extends GenericPayloadSerializer<StartSession> {

    public StartSessionSerializer() {
        super(StartSession.class);
    }

    @Override
    protected String serializePayload(StartSession payload) {
        return new XMLElement("session", "urn:ietf:params:xml:ns:xmpp-session").serialize();
    }

}
