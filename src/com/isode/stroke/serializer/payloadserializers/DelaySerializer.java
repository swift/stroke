/*
* Copyright (c) 2014 Kevin Smith and Remko Tronçon
* All rights reserved.
*/

/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/

package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.base.DateTime;
import com.isode.stroke.elements.Delay;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLElement;

public class DelaySerializer extends GenericPayloadSerializer<Delay> {
    public DelaySerializer() {
        super(Delay.class);
    }

    protected String serializePayload(Delay delay) {
        XMLElement delayElement = new XMLElement("delay", "urn:xmpp:delay");
        if (delay.getFrom() != null && delay.getFrom().isValid()) {
            delayElement.setAttribute("from", delay.getFrom().toString());
        }
        delayElement.setAttribute("stamp", DateTime.dateToString(delay.getStamp()));
        return delayElement.serialize();
    }
}
