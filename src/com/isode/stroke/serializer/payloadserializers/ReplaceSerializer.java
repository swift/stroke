/*
 * Copyright (c) 2011 Vlad Voicu
 * Licensed under the Simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
/*
 * Copyright (c) 2015 Thomas Graviou
 * Licensed under the Simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
 
package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.Replace;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLTextNode;

class ReplaceSerializer extends GenericPayloadSerializer<Replace> {
	
	public ReplaceSerializer() {
		super(Replace.class);
	}
		
	protected String serializePayload(Replace replace) {
		return "<replace id = '" + 
			replace.getID() + 
			"' xmlns='urn:xmpp:message-correct:0'/>";
	}
	
}
