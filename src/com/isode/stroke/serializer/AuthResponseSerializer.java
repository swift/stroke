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
import com.isode.stroke.elements.AuthResponse;
import com.isode.stroke.elements.Element;
import com.isode.stroke.stringcodecs.Base64;

class AuthResponseSerializer extends GenericElementSerializer<AuthResponse> {

    public AuthResponseSerializer() {
        super(AuthResponse.class);
    }

    public String serialize(Element element) {
        AuthResponse authResponse = (AuthResponse) element;
        String value = "";
        ByteArray message = authResponse.getValue();
        if (message != null) {
            if (message.isEmpty()) {
                value = "";
            } else {
                value = Base64.encode(message);
            }
        }
        return "<response xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">" + value + "</response>";
    }
}
