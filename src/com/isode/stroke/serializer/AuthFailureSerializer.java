/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.serializer;

import com.isode.stroke.elements.AuthFailure;
import com.isode.stroke.elements.Element;
import com.isode.stroke.serializer.xml.XMLElement;

class AuthFailureSerializer extends GenericElementSerializer<AuthFailure>{

    public AuthFailureSerializer() {
        super(AuthFailure.class);
    }

    public String serialize(Element element) {
        return new XMLElement("failure", "urn:ietf:params:xml:ns:xmpp-sasl").serialize();
    }

}
