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
import com.isode.stroke.elements.StanzaAck;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.base.SafeByteArray;

public class StanzaAckSerializer extends GenericElementSerializer<StanzaAck> {

    public StanzaAckSerializer() {
        super(StanzaAck.class);
    }

    public SafeByteArray serialize(Element element) {
        StanzaAck stanzaAck = (StanzaAck) element;
        assert stanzaAck.isValid();
        XMLElement result = new XMLElement("a", "urn:xmpp:sm:2");
        result.setAttribute("h", Long.toString(stanzaAck.getHandledStanzasCount()));
        return new SafeByteArray(result.serialize());
    }
}
