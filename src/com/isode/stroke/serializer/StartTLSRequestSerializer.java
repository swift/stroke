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
import com.isode.stroke.elements.StartTLSRequest;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.base.SafeByteArray;

public class StartTLSRequestSerializer extends GenericElementSerializer<StartTLSRequest> {

    public StartTLSRequestSerializer() {
        super(StartTLSRequest.class);
    }

    public SafeByteArray serialize(Element element) {
        return new SafeByteArray(new XMLElement("starttls", "urn:ietf:params:xml:ns:xmpp-tls").serialize());
    }

}
