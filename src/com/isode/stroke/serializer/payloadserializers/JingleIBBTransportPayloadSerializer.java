/*
 * Copyright (c) 2011 Tobias Markmann
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.serializer.GenericPayloadSerializer;

import com.isode.stroke.serializer.xml.XMLRawTextNode;
import com.isode.stroke.serializer.xml.XMLNode;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.elements.JingleIBBTransportPayload;
import com.isode.stroke.base.NotNull;

public class JingleIBBTransportPayloadSerializer extends GenericPayloadSerializer<JingleIBBTransportPayload> {

	public JingleIBBTransportPayloadSerializer() {
		super(JingleIBBTransportPayload.class);
	}

	public String serializePayload(JingleIBBTransportPayload payload) {
		XMLElement payloadXML = new XMLElement("transport", "urn:xmpp:jingle:transports:ibb:1");
		if (payload.getBlockSize() > 0) {
			payloadXML.setAttribute("block-size", Integer.toString(payload.getBlockSize()));
		}
		payloadXML.setAttribute("sid", payload.getSessionID());

		return payloadXML.serialize();
	}
}