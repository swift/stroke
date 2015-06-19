/*
 * Copyright (c) 2015 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
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
import com.isode.stroke.elements.Message;
import com.isode.stroke.elements.Thread;
import com.isode.stroke.elements.Forwarded;
import com.isode.stroke.elements.CarbonsEnable;
import com.isode.stroke.elements.CarbonsDisable;
import com.isode.stroke.elements.CarbonsReceived;
import com.isode.stroke.elements.CarbonsSent;
import com.isode.stroke.elements.CarbonsPrivate;
import com.isode.stroke.jid.JID;
import com.isode.stroke.parser.payloadparsers.PayloadsParserTester;
import com.isode.stroke.eventloop.DummyEventLoop;

public class CarbonsParserTest {

	public CarbonsParserTest() {

	}

	/*
	 * Test parsing of example 3 in XEP-0280.
	 */
	@Test
	public void testParseExample3() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse("<enable xmlns='urn:xmpp:carbons:2' />"));

		CarbonsEnable enable = (CarbonsEnable)parser.getPayload();
		assertNotNull(enable);
	}

	/*
	 * Test parsing of example 6 in XEP-0280.
	 */
	@Test
	public void testParseExample6() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse("<disable xmlns='urn:xmpp:carbons:2' />"));

		CarbonsDisable disable = (CarbonsDisable)parser.getPayload();
		assertNotNull(disable);
	}

	/*
	 * Test parsing of example 12 in XEP-0280.
	*/
	@Test
	public void testParseExample12() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse("<received xmlns='urn:xmpp:carbons:2'>" +
										"<forwarded xmlns='urn:xmpp:forward:0'>" +
											"<message xmlns='jabber:client'" +
												" from='juliet@capulet.example/balcony'" +
												" to='romeo@montague.example/garden'" +
												" type='chat'>" +
												"<body>What man art thou that, thus bescreen'd in night, so stumblest on my counsel?</body>" +
												"<thread>0e3141cd80894871a68e6fe6b1ec56fa</thread>" +
											"</message>" +
										"</forwarded>" +
									"</received>"));

		CarbonsReceived received = (CarbonsReceived)parser.getPayload();
		assertNotNull(received);

		Forwarded forwarded = received.getForwarded();
		assertNotNull(forwarded);

		Message message = (Message)(forwarded.getStanza());
		assertNotNull(message);
		assertEquals(new JID("juliet@capulet.example/balcony"), message.getFrom());

		Thread thread = message.getPayload(new Thread());
		assertNotNull(thread);
		assertEquals("0e3141cd80894871a68e6fe6b1ec56fa", thread.getText());
	}

	/*
	 * Test parsing of example 14 in XEP-0280.
	 */
	@Test
	public void testParseExample14() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse("<sent xmlns='urn:xmpp:carbons:2'>" +
										"<forwarded xmlns='urn:xmpp:forward:0'>" +
											"<message xmlns='jabber:client'" +
												" to='juliet@capulet.example/balcony'" +
												" from='romeo@montague.example/home'" +
												" type='chat'>" +
												"<body>Neither, fair saint, if either thee dislike.</body>" +
												"<thread>0e3141cd80894871a68e6fe6b1ec56fa</thread>" +
											"</message>" +
										"</forwarded>" +
									"</sent>"));

		CarbonsSent sent = (CarbonsSent)parser.getPayload();
		assertNotNull(sent);

		Forwarded forwarded = sent.getForwarded();
		assertNotNull(forwarded);

		Message message = (Message)(forwarded.getStanza());
		assertNotNull(message);
		assertEquals(new JID("juliet@capulet.example/balcony"), message.getTo());
	}

	/*
	 * Test parsing of example 15 in XEP-0280.
	 */
	@Test
	public void testParseExample15() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse("<private xmlns='urn:xmpp:carbons:2'/>"));

		CarbonsPrivate privae = (CarbonsPrivate)parser.getPayload();
		assertNotNull(privae);
	}
}