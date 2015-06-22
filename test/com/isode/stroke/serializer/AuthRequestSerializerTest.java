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
import com.isode.stroke.serializer.AuthRequestSerializer;
import com.isode.stroke.elements.AuthRequest;
import com.isode.stroke.base.ByteArray;

public class AuthRequestSerializerTest {

	/**
	* Default Constructor.
	*/
	public AuthRequestSerializerTest() {

	}

	@Test
	public void testSerialize() {
		AuthRequestSerializer testling = new AuthRequestSerializer();
		AuthRequest authRequest = new AuthRequest("PLAIN");
		authRequest.setMessage(new ByteArray("foo"));

		assertEquals(
			"<auth xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\" mechanism=\"PLAIN\">" +
				"Zm9v" +
			"</auth>", testling.serialize(authRequest));
	}

	@Test
	public void testSerialize_NoMessage() {
		AuthRequestSerializer testling = new AuthRequestSerializer();
		AuthRequest authRequest = new AuthRequest("PLAIN");

		assertEquals(
			"<auth xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\" mechanism=\"PLAIN\">" +
			"</auth>", testling.serialize(authRequest));
	}

	@Test
	public void testSerialize_EmptyMessage() {
		AuthRequestSerializer testling = new AuthRequestSerializer();
		AuthRequest authRequest = new AuthRequest("PLAIN");
		authRequest.setMessage(new ByteArray());

		assertEquals(
			"<auth xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\" mechanism=\"PLAIN\">" +
				"=" +
			"</auth>", testling.serialize(authRequest));
	}
}