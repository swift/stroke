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
import com.isode.stroke.elements.AuthSuccess;
import com.isode.stroke.elements.Element;
import com.isode.stroke.stringcodecs.Base64;


public class AuthSuccessSerializer extends GenericElementSerializer<AuthSuccess> {

    public AuthSuccessSerializer() {
        super(AuthSuccess.class);
    }

    public SafeByteArray serialize(Element element) {
        AuthSuccess authSuccess = (AuthSuccess)element;
	String value = "";
	ByteArray message = authSuccess.getValue();
	if (message != null) {
		if (message.isEmpty()) {
			value = "=";
		}
		else {
			value = Base64.encode(message);
		}
	}
	return new SafeByteArray("<success xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">" + value + "</success>");
    }

}
