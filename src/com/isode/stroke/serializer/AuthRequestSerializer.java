/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.serializer;

import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.elements.AuthRequest;
import com.isode.stroke.elements.Element;
import com.isode.stroke.stringcodecs.Base64;

public class AuthRequestSerializer extends GenericElementSerializer<AuthRequest> {

    public AuthRequestSerializer() {
        super(AuthRequest.class);
    }

    public SafeByteArray serialize(Element element) {
        AuthRequest authRequest = (AuthRequest)element;
	SafeByteArray value = new SafeByteArray();
	SafeByteArray message = authRequest.getMessage();
	if (message != null) {
		if (message.isEmpty()) {
			value = new SafeByteArray("=");
		}
		else {
			value = Base64.encode(message);
		}
	}
	return new SafeByteArray("<auth xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\" mechanism=\"" + authRequest.getMechanism() + "\">").append(value).append("</auth>");
    }

}