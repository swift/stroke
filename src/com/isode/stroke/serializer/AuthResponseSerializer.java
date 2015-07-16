/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.serializer;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.elements.AuthResponse;
import com.isode.stroke.elements.Element;
import com.isode.stroke.stringcodecs.Base64;

public class AuthResponseSerializer extends GenericElementSerializer<AuthResponse> {

    public AuthResponseSerializer() {
        super(AuthResponse.class);
    }

    public SafeByteArray serialize(Element element) {
        AuthResponse authResponse = (AuthResponse) element;
        SafeByteArray value = new SafeByteArray();
        SafeByteArray message = authResponse.getValue();
        if (message != null) {
            if (message.isEmpty()) {
                value = new SafeByteArray("");
            } else {
                value = Base64.encode(message);
            }
        }
        return new SafeByteArray("<response xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">").append(value).append("</response>");
    }
}
