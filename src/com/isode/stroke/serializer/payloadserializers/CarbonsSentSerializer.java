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
import com.isode.stroke.elements.CarbonsSent;
import com.isode.stroke.base.NotNull;

public class CarbonsSentSerializer extends GenericPayloadSerializer<CarbonsSent> {

	private PayloadSerializerCollection serializers_;

	public CarbonsSentSerializer(PayloadSerializerCollection serializers) {
		super(CarbonsSent.class);
		this.serializers_ = serializers;
	}

	public String serializePayload(CarbonsSent sent) {
		XMLElement element = new XMLElement("sent", "urn:xmpp:carbons:2");
		if (sent.getForwarded() != null) {
			element.addNode(new XMLRawTextNode(new ForwardedSerializer(serializers_).serialize(sent.getForwarded())));
		}
		return element.serialize();
	}
}