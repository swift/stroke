/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.parser.payloadparsers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import com.isode.stroke.elements.JingleIBBTransportPayload;
import com.isode.stroke.parser.payloadparsers.JingleIBBTransportMethodPayloadParser;
import com.isode.stroke.parser.payloadparsers.PayloadsParserTester;
import com.isode.stroke.eventloop.DummyEventLoop;

public class JingleIBBTransportMethodPayloadParserTest {

	public JingleIBBTransportMethodPayloadParserTest() {

	}

	@Test
	public void testParse() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse("<transport block-size=\"4\" sid=\"546-45\" xmlns=\"urn:xmpp:jingle:transports:ibb:1\"/>"));

		JingleIBBTransportPayload payload = (JingleIBBTransportPayload)parser.getPayload();
		assertEquals(Integer.valueOf(4), payload.getBlockSize());
		assertEquals("546-45", payload.getSessionID());
	}
}