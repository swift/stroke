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

package com.isode.stroke.parser.payloadparsers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import com.isode.stroke.elements.ResourceBind;
import com.isode.stroke.parser.payloadparsers.ResourceBindParser;
import com.isode.stroke.parser.payloadparsers.PayloadsParserTester;
import com.isode.stroke.eventloop.DummyEventLoop;
import com.isode.stroke.jid.JID;

public class ResourceBindParserTest {

	public ResourceBindParserTest() {

	}

	@Test
	public void testParse_JID() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);

		assertNotNull(parser.parse("<bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'><jid>somenode@example.com/someresource</jid></bind>"));

		ResourceBind payload = (ResourceBind)(parser.getPayload());
		assertEquals(new JID("somenode@example.com/someresource"), payload.getJID());
	}

	@Test
	public void testParse_Resource() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);

		assertNotNull(parser.parse("<bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'><resource>someresource</resource></bind>"));

		ResourceBind payload = (ResourceBind)(parser.getPayload());
		assertEquals("someresource", payload.getResource());
	}
}