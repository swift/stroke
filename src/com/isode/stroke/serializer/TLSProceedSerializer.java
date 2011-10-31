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
import com.isode.stroke.elements.TLSProceed;
import com.isode.stroke.serializer.xml.XMLElement;

class TLSProceedSerializer extends GenericElementSerializer<TLSProceed>{

    public TLSProceedSerializer() {
        super(TLSProceed.class);
    }

    public String serialize(Element element) {
        return new XMLElement("proceed", "urn:ietf:params:xml:ns:xmpp-tls").serialize();
    }

}
