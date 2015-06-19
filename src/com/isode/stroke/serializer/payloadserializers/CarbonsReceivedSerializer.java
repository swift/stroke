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
import com.isode.stroke.serializer.PayloadSerializerCollection;
import com.isode.stroke.serializer.xml.XMLRawTextNode;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.elements.CarbonsReceived;
import com.isode.stroke.base.NotNull;

public class CarbonsReceivedSerializer extends GenericPayloadSerializer<CarbonsReceived> {

	private PayloadSerializerCollection serializers_;

	public CarbonsReceivedSerializer(PayloadSerializerCollection serializers) {
		super(CarbonsReceived.class);
		this.serializers_ = serializers;
	}

	public String serializePayload(CarbonsReceived received) {
		XMLElement element = new XMLElement("received", "urn:xmpp:carbons:2");
		if (received.getForwarded() != null) {
			element.addNode(new XMLRawTextNode(new ForwardedSerializer(serializers_).serialize(received.getForwarded())));
		}
		return element.serialize();
	}
}