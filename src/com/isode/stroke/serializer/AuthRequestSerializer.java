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
import com.isode.stroke.elements.AuthRequest;
import com.isode.stroke.elements.Element;
import com.isode.stroke.stringcodecs.Base64;

class AuthRequestSerializer extends GenericElementSerializer<AuthRequest> {

    public AuthRequestSerializer() {
        super(AuthRequest.class);
    }

    public String serialize(Element element) {
        AuthRequest authRequest = (AuthRequest)element;
	String value = "";
	ByteArray message = authRequest.getMessage();
	if (message != null) {
		if (message.isEmpty()) {
			value = "=";
		}
		else {
			value = Base64.encode(message);
		}
	}
	return "<auth xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\" mechanism=\"" + authRequest.getMechanism() + "\">" + value + "</auth>";
    }

}
