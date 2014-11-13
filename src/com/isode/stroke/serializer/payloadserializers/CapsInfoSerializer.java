/*
 * Copyright (c) 2010-2015 Isode Limited.
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
import com.isode.stroke.elements.CapsInfo;
import com.isode.stroke.base.NotNull;
import com.isode.stroke.serializer.xml.XMLElement;

public class CapsInfoSerializer extends GenericPayloadSerializer<CapsInfo> {

	/**
	* CapsInfoSerializer();
	*/
	public CapsInfoSerializer() {
		super(CapsInfo.class);
	}

	@Override
	protected String serializePayload(CapsInfo capsInfo) {
		NotNull.exceptIfNull(capsInfo, "capsInfo");
		XMLElement capsElement = new XMLElement("c", "http://jabber.org/protocol/caps");
		capsElement.setAttribute("node", capsInfo.getNode());
		capsElement.setAttribute("hash", capsInfo.getHash());
		capsElement.setAttribute("ver", capsInfo.getVersion());
		return capsElement.serialize();
	}
} 
