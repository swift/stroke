/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.parser.payloadparsers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import com.isode.stroke.elements.Bytestreams;
import com.isode.stroke.jid.JID;
import com.isode.stroke.parser.payloadparsers.BytestreamsParser;
import com.isode.stroke.parser.payloadparsers.PayloadsParserTester;
import com.isode.stroke.eventloop.DummyEventLoop;

public class BytestreamsParserTest {

	public BytestreamsParserTest() {

	}

	@Test
	public void testParse() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse("<query sid=\"hello\" xmlns=\"http://jabber.org/protocol/bytestreams\">" +
								"<streamhost host=\"blah.xyz.edu\" jid=\"user1@bar.com/bla\" port=\"445\"/>" +
								"<streamhost host=\"bal.zyx.ude\" jid=\"user1@baz.com/bal\" port=\"449\"/>" +
								"</query>"));

		Bytestreams payload = (Bytestreams)parser.getPayload();
		assertEquals("hello", payload.getStreamID());

		assertEquals(2, payload.getStreamHosts().size());

		assertEquals("blah.xyz.edu", payload.getStreamHosts().get(0).getHost());
		assertEquals(new JID("user1@bar.com/bla"), payload.getStreamHosts().get(0).getJID());
		assertEquals(445, payload.getStreamHosts().get(0).getPort());

		assertEquals("bal.zyx.ude", payload.getStreamHosts().get(1).getHost());
		assertEquals(new JID("user1@baz.com/bal"), payload.getStreamHosts().get(1).getJID());
		assertEquals(449, payload.getStreamHosts().get(1).getPort());
	}
}