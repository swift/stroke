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
import com.isode.stroke.serializer.AuthSuccessSerializer;
import com.isode.stroke.elements.AuthSuccess;
import com.isode.stroke.base.ByteArray;

public class AuthSuccessSerializerTest {

	/**
	* Default Constructor.
	*/
	public AuthSuccessSerializerTest() {

	}

	@Test
	public void testSerialize() {
		AuthSuccessSerializer testling = new AuthSuccessSerializer();
		AuthSuccess authSuccess = new AuthSuccess();
		authSuccess.setValue(new ByteArray("foo"));

		assertEquals(
			"<success xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">" +
				"Zm9v" +
			"</success>", testling.serialize(authSuccess));
	}

	@Test
	public void testSerialize_NoMessage() {
		AuthSuccessSerializer testling = new AuthSuccessSerializer();
		AuthSuccess authSuccess = new AuthSuccess();

		assertEquals(
			"<success xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">" +
			"</success>", testling.serialize(authSuccess));
	}

	@Test
	public void testSerialize_EmptyMessage() {
		AuthSuccessSerializer testling = new AuthSuccessSerializer();
		AuthSuccess authSuccess = new AuthSuccess();
		authSuccess.setValue(new ByteArray());

		assertEquals(
			"<success xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">" +
				"=" +
			"</success>", testling.serialize(authSuccess));
	}
}