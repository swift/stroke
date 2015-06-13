/*
 * Copyright (c) 2014 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.serializer.payloadserializers.ForwardedSerializer;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLRawTextNode;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.serializer.PayloadSerializerCollection;
import com.isode.stroke.serializer.PayloadSerializer;
import com.isode.stroke.elements.IsodeIQDelegation;
import com.isode.stroke.elements.Forwarded;
import com.isode.stroke.base.NotNull;

public class IsodeIQDelegationSerializer extends GenericPayloadSerializer<IsodeIQDelegation> {

	private PayloadSerializerCollection serializers;

	public IsodeIQDelegationSerializer(PayloadSerializerCollection serializers) {
		super(IsodeIQDelegation.class);
		this.serializers = serializers;
	}

	public String serializePayload(IsodeIQDelegation payload) {
		if (payload == null) {
			return "";
		}
		XMLElement element = new XMLElement("delegate", "http://isode.com/iq_delegation");
		element.addNode(new XMLRawTextNode((new ForwardedSerializer(serializers)).serialize((Forwarded)payload.getForward())));
		return element.serialize();
	}
}