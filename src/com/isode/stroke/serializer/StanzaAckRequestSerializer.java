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
import com.isode.stroke.elements.StanzaAckRequest;
import com.isode.stroke.serializer.xml.XMLElement;

class StanzaAckRequestSerializer extends GenericElementSerializer<StanzaAckRequest> {

    public StanzaAckRequestSerializer() {
        super(StanzaAckRequest.class);
    }

    public String serialize(Element element) {
        return new XMLElement("r", "urn:xmpp:sm:2").serialize();
    }

}
