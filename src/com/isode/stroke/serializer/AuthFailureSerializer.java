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
import com.isode.stroke.base.SafeByteArray;

class AuthFailureSerializer extends GenericElementSerializer<AuthFailure>{

    public AuthFailureSerializer() {
        super(AuthFailure.class);
    }

    public SafeByteArray serialize(Element element) {
        return new SafeByteArray(new XMLElement("failure", "urn:ietf:params:xml:ns:xmpp-sasl").serialize());
    }

}
