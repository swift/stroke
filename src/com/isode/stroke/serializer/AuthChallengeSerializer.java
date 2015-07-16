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
import com.isode.stroke.elements.AuthChallenge;
import com.isode.stroke.elements.Element;
import com.isode.stroke.stringcodecs.Base64;
import com.isode.stroke.base.SafeByteArray;

public class AuthChallengeSerializer extends GenericElementSerializer<AuthChallenge> {

    public AuthChallengeSerializer() {
        super(AuthChallenge.class);
    }

    public SafeByteArray serialize(Element element) {
        AuthChallenge authChallenge = (AuthChallenge)element;
        String value = "";
	ByteArray message = authChallenge.getValue();
	if (message != null) {
		if (message.isEmpty()) {
			value = "=";
		}
		else {
			value = Base64.encode(message);
		}
	}
	return new SafeByteArray("<challenge xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">" + value + "</challenge>");
    }

}
