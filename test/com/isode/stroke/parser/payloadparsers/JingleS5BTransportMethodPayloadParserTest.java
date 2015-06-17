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
import com.isode.stroke.elements.JingleS5BTransportPayload;
import com.isode.stroke.elements.JingleTransportPayload;
import com.isode.stroke.parser.payloadparsers.JingleS5BTransportMethodPayloadParser;
import com.isode.stroke.parser.payloadparsers.PayloadsParserTester;
import com.isode.stroke.eventloop.DummyEventLoop;
import com.isode.stroke.jid.JID;

public class JingleS5BTransportMethodPayloadParserTest {

	public JingleS5BTransportMethodPayloadParserTest() {

	}

	@Test
	public void testParse() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse("<transport dstaddr=\"UK\" mode=\"tcp\" sid=\"\" xmlns=\"urn:xmpp:jingle:transports:s5b:1\">" +
								"<candidate cid=\"cid\" host=\"173.194.36.112\" jid=\"blas@nal.vx\" port=\"-1\" priority=\"4\" type=\"assisted\"/>" +
								"<proxy-error/><activated cid=\"Activity\"/><candidate-used cid=\"Candidate\"/></transport>"));

		JingleS5BTransportPayload payload = (JingleS5BTransportPayload)parser.getPayload();
		assertEquals (JingleS5BTransportPayload.Mode.TCPMode, payload.getMode());
		assertEquals ("Candidate", payload.getCandidateUsed());
		assertEquals ("Activity", payload.getActivated());
		assertEquals ("UK", payload.getDstAddr());
		assertEquals (false, payload.hasCandidateError());
		assertEquals (true, payload.hasProxyError());

		assertEquals(1, payload.getCandidates().size());
		JingleS5BTransportPayload.Candidate candidate = payload.getCandidates().get(0);
		assertEquals("cid", candidate.cid);
		assertEquals(new JID("blas@nal.vx"), candidate.jid);
		assertEquals("/173.194.36.112", candidate.hostPort.getAddress().getInetAddress().toString());
		assertEquals(4, candidate.priority);
		assertEquals(JingleS5BTransportPayload.Candidate.Type.AssistedType, candidate.type);
	}
}