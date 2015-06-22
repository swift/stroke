/*
 * Copyright (c) 2010 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.serializer.payloadserializers.FullPayloadSerializerCollection;
import com.isode.stroke.serializer.PayloadSerializer;
import com.isode.stroke.elements.Payload;

public class PayloadsSerializer {

	private FullPayloadSerializerCollection serializers = new FullPayloadSerializerCollection();

	public String serialize(Payload payload) {
		PayloadSerializer serializer = serializers.getPayloadSerializer(payload);
		if (serializer != null) {
			return serializer.serialize(payload);
		}
		else {
			assert(false);
			return "";
		}
	}
}