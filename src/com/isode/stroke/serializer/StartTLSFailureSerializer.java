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
import com.isode.stroke.elements.StartTLSFailure;
import com.isode.stroke.serializer.xml.XMLElement;

class StartTLSFailureSerializer extends GenericElementSerializer<StartTLSFailure> {

    public StartTLSFailureSerializer() {
        super(StartTLSFailure.class);
    }

    public String serialize(Element element) {
        return new XMLElement("failure", "urn:ietf:params:xml:ns:xmpp-tls").serialize();
    }

}
