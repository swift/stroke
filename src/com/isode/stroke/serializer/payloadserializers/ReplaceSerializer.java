/*
 * Copyright (c) 2012-2015, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2011 Vlad Voicu
 * Licensed under the Simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.Replace;
import com.isode.stroke.serializer.GenericPayloadSerializer;

public class ReplaceSerializer extends GenericPayloadSerializer<Replace> {

	public ReplaceSerializer() {
		super(Replace.class);
	}

    protected String serializePayload(Replace replace) {
    	return "<replace id = '" + replace.getID() + "' xmlns='urn:xmpp:message-correct:0'/>";
    }

}
