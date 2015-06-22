/*
 * Copyright (c) 2010-2012 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.serializer.payloadserializers;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.BeforeClass;
import com.isode.stroke.serializer.payloadserializers.ErrorSerializer;
import com.isode.stroke.serializer.payloadserializers.FullPayloadSerializerCollection;
import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.elements.Delay;

public class ErrorSerializerTest {

	@BeforeClass
	public static void init() throws Exception {
	}

	private FullPayloadSerializerCollection serializers = new FullPayloadSerializerCollection();

	/**
	* Default Constructor.
	*/
	public ErrorSerializerTest() {

	}

	@Test
	public void testSerialize() {
		ErrorSerializer testling = new ErrorSerializer(serializers);
		ErrorPayload error = new ErrorPayload(ErrorPayload.Condition.BadRequest, ErrorPayload.Type.Cancel, "My Error");

		assertEquals("<error type=\"cancel\"><bad-request xmlns=\"urn:ietf:params:xml:ns:xmpp-stanzas\"/><text xmlns=\"urn:ietf:params:xml:ns:xmpp-stanzas\">My Error</text></error>", testling.serialize(error));
	}

	@Test
	public void testSerialize_Payload() {
		ErrorSerializer testling = new ErrorSerializer(serializers);
		ErrorPayload error = new ErrorPayload();
		error.setPayload(new Delay());

		assertEquals(
				"<error type=\"cancel\"><undefined-condition xmlns=\"urn:ietf:params:xml:ns:xmpp-stanzas\"/><delay xmlns=\"urn:xmpp:delay\"/></error>"
				, testling.serialize(error));
	}
}