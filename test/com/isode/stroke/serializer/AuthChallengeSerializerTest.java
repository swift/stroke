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
import com.isode.stroke.serializer.AuthChallengeSerializer;
import com.isode.stroke.elements.AuthChallenge;
import com.isode.stroke.base.ByteArray;

public class AuthChallengeSerializerTest {

	/**
	* Default Constructor.
	*/
	public AuthChallengeSerializerTest() {

	}

	@Test
	public void testSerialize() {
		AuthChallengeSerializer testling = new AuthChallengeSerializer();
		AuthChallenge authChallenge = new AuthChallenge();
		authChallenge.setValue(new ByteArray("foo"));

		assertEquals(
			"<challenge xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">" +
				"Zm9v" +
			"</challenge>", testling.serialize(authChallenge));
	}

	@Test
	public void testSerialize_NoMessage() {
		AuthChallengeSerializer testling = new AuthChallengeSerializer();
		AuthChallenge authChallenge = new AuthChallenge();

		assertEquals(
			"<challenge xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\"></challenge>", testling.serialize(authChallenge));
	}

	@Test
	public void testSerialize_EmptyMessage() {
		AuthChallengeSerializer testling = new AuthChallengeSerializer();
		AuthChallenge authChallenge = new AuthChallenge();
		authChallenge.setValue(new ByteArray());

		assertEquals(
			"<challenge xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">" +
				"=" +
			"</challenge>", testling.serialize(authChallenge));
	}
}