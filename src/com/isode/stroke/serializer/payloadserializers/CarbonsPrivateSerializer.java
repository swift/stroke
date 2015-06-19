/*
 * Copyright (c) 2015 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLTextNode;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.elements.CarbonsPrivate;
import com.isode.stroke.base.NotNull;

public class CarbonsPrivateSerializer extends GenericPayloadSerializer<CarbonsPrivate> {

	public CarbonsPrivateSerializer() {
		super(CarbonsPrivate.class);
	}

	public String serializePayload(CarbonsPrivate payload) {
		XMLElement element = new XMLElement("private", "urn:xmpp:carbons:2");
		return element.serialize();
	}
}