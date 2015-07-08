/*
 * Copyright (c) 2011-2015 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.queries;

import com.isode.stroke.queries.Request;
import com.isode.stroke.elements.RawXMLPayload;
import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.jid.JID;
import com.isode.stroke.serializer.PayloadSerializer;
import com.isode.stroke.serializer.payloadserializers.ErrorSerializer;
import com.isode.stroke.serializer.payloadserializers.FullPayloadSerializerCollection;
import com.isode.stroke.signals.Signal1;
import com.isode.stroke.signals.SignalConnection;

public class RawRequest extends Request {

	private FullPayloadSerializerCollection serializers = new FullPayloadSerializerCollection();

	public RawRequest(IQ.Type type, final JID receiver, final String data, IQRouter router) {
		super(type, receiver, new RawXMLPayload(data), router);
	}

	public static RawRequest create(IQ.Type type, final JID recipient, final String data, IQRouter router) {
		return new RawRequest(type, recipient, data, router);
	}

	public final Signal1<String> onResponse = new Signal1<String>();

	protected void handleResponse(Payload payload, ErrorPayload error) {
		if (error != null) {
			onResponse.emit(new ErrorSerializer(serializers).serializePayload(error));
		}
		else {
			assert(payload != null);
			PayloadSerializer serializer = serializers.getPayloadSerializer(payload);
			assert(serializer != null);
			onResponse.emit(serializer.serialize(payload));
		}
	}
}

