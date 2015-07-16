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
import com.isode.stroke.base.SafeByteArray;

public class StanzaAckRequestSerializer extends GenericElementSerializer<StanzaAckRequest> {

    public StanzaAckRequestSerializer() {
        super(StanzaAckRequest.class);
    }

    public SafeByteArray serialize(Element element) {
        return new SafeByteArray(new XMLElement("r", "urn:xmpp:sm:2").serialize());
    }

}
