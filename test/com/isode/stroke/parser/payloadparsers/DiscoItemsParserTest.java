/*
 * Copyright (c) 2012 Jan Kaluza
 * Licensed under the Simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
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
import com.isode.stroke.elements.DiscoItems;
import com.isode.stroke.parser.payloadparsers.DiscoItemsParser;
import com.isode.stroke.parser.payloadparsers.PayloadsParserTester;
import com.isode.stroke.eventloop.DummyEventLoop;

public class DiscoItemsParserTest {

	public DiscoItemsParserTest() {

	}

	@Test
	public void testParse() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);

		assertNotNull(parser.parse(
			"<query xmlns='http://jabber.org/protocol/disco#items' node='http://jabber.org/protocol/commands'>" +
				"<item jid='responder@domain' node='list' name='List Service Configurations'/>" +
				"<item jid='responder@domain' node='config' name='Configure Service'/>" +
			"</query>"));

		DiscoItems payload =(DiscoItems)(parser.getPayload());
		assertEquals(2, payload.getItems().size());
		assertEquals("List Service Configurations", payload.getItems().get(0).getName());
		assertEquals("list", payload.getItems().get(0).getNode());
		assertEquals("responder@domain", payload.getItems().get(0).getJID().toString());
		assertEquals("Configure Service", payload.getItems().get(1).getName());
		assertEquals("config", payload.getItems().get(1).getNode());
		assertEquals("responder@domain", payload.getItems().get(1).getJID().toString());
		assertEquals("http://jabber.org/protocol/commands", payload.getNode());
	}
}