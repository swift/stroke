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

class StanzaAckSerializer extends GenericElementSerializer<StanzaAck> {

    public StanzaAckSerializer() {
        super(StanzaAck.class);
    }

    public String serialize(Element element) {
        StanzaAck stanzaAck = (StanzaAck) element;
        assert stanzaAck.isValid();
        XMLElement result = new XMLElement("a", "urn:xmpp:sm:2");
        result.setAttribute("h", Double.toString(stanzaAck.getHandledStanzasCount()));
        return result.serialize();
    }
}
