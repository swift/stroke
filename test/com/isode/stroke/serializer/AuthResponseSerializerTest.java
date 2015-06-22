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

package com.isode.stroke.serializer;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.isode.stroke.serializer.AuthResponseSerializer;
import com.isode.stroke.elements.AuthResponse;
import com.isode.stroke.base.ByteArray;

public class AuthResponseSerializerTest {

	/**
	* Default Constructor.
	*/
	public AuthResponseSerializerTest() {

	}

	@Test
	public void testSerialize() {
		AuthResponseSerializer testling = new AuthResponseSerializer();
		AuthResponse authResponse = new AuthResponse();
		authResponse.setValue(new ByteArray("foo"));

		assertEquals(
			"<response xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">" +
				"Zm9v" +
			"</response>", testling.serialize(authResponse));
	}

	@Test
	public void testSerialize_NoMessage() {
		AuthResponseSerializer testling = new AuthResponseSerializer();
		AuthResponse authResponse = new AuthResponse();

		assertEquals(
			"<response xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">" +
			"</response>", testling.serialize(authResponse));
	}

	@Test
	public void testSerialize_EmptyMessage() {
		AuthResponseSerializer testling = new AuthResponseSerializer();
		AuthResponse authResponse = new AuthResponse();
		authResponse.setValue(new ByteArray());

		assertEquals(
			"<response xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">" +
				"" +
			"</response>", testling.serialize(authResponse));
	}
}