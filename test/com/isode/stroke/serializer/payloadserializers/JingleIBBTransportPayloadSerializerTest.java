/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.serializer.payloadserializers;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.isode.stroke.serializer.payloadserializers.JingleIBBTransportPayloadSerializer;
import com.isode.stroke.elements.JingleIBBTransportPayload;

public class JingleIBBTransportPayloadSerializerTest {

	/**
	* Default Constructor.
	*/
	public JingleIBBTransportPayloadSerializerTest() {

	}

	@Test
	public void testSerialize() {
		JingleIBBTransportPayloadSerializer testling = new JingleIBBTransportPayloadSerializer();
		JingleIBBTransportPayload jingleIBBTransportPayload = new JingleIBBTransportPayload();
		jingleIBBTransportPayload.setBlockSize(4);
		jingleIBBTransportPayload.setSessionID("546-45");
		String expectedResult = "<transport block-size=\"4\" sid=\"546-45\" xmlns=\"urn:xmpp:jingle:transports:ibb:1\"/>";
		assertEquals(expectedResult, testling.serialize(jingleIBBTransportPayload));
	}
}