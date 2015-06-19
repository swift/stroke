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
import com.isode.stroke.elements.CarbonsEnable;
import com.isode.stroke.base.NotNull;

public class CarbonsEnableSerializer extends GenericPayloadSerializer<CarbonsEnable> {

	public CarbonsEnableSerializer() {
		super(CarbonsEnable.class);
	}

	public String serializePayload(CarbonsEnable payload) {
		XMLElement element = new XMLElement("enable", "urn:xmpp:carbons:2");
		return element.serialize();
	}
}