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
import com.isode.stroke.elements.SecurityLabelsCatalog;
import com.isode.stroke.parser.payloadparsers.SecurityLabelsCatalogParser;
import com.isode.stroke.parser.payloadparsers.PayloadsParserTester;
import com.isode.stroke.eventloop.DummyEventLoop;
import com.isode.stroke.jid.JID;

public class SecurityLabelsCatalogParserTest {

	public SecurityLabelsCatalogParserTest() {

	}

	@Test
	public void testParse() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);

		assertNotNull(parser.parse(
			"<catalog desc=\"an example set of labels\" name=\"Default\" to=\"example.com\" xmlns=\"urn:xmpp:sec-label:catalog:2\">"
			+	 "<item selector='Classified|SECRET'>"
			+		"<securitylabel xmlns=\"urn:xmpp:sec-label:0\">"
			+			"<displaymarking bgcolor=\"red\" fgcolor=\"black\">SECRET</displaymarking>"
			+			"<label><esssecuritylabel xmlns=\"urn:xmpp:sec-label:ess:0\">MQYCAQQGASk=</esssecuritylabel></label>"
			+		"</securitylabel>"
			+	 "</item>"
			+	 "<item selector='Classified|CONFIDENTIAL' default='true'>"
			+		"<securitylabel xmlns=\"urn:xmpp:sec-label:0\">"
			+			"<displaymarking bgcolor=\"navy\" fgcolor=\"black\">CONFIDENTIAL</displaymarking>"
			+			"<label><esssecuritylabel xmlns=\"urn:xmpp:sec-label:ess:0\">MQMGASk=</esssecuritylabel></label>"
			+		"</securitylabel>"
			+	 "</item>"
			+	 "<item selector='Unclassified|UNCLASSIFIED'/>"
			+	"</catalog>"));

		SecurityLabelsCatalog payload = (SecurityLabelsCatalog)(parser.getPayload());
		assertEquals("Default", payload.getName());
		assertEquals("an example set of labels", payload.getDescription());
		assertEquals(new JID("example.com"), payload.getTo());
		assertEquals(3, payload.getItems().size());
		assertEquals("SECRET", payload.getItems().get(0).getLabel().getDisplayMarking());
		assertEquals("<esssecuritylabel xmlns=\"urn:xmpp:sec-label:ess:0\">MQYCAQQGASk=</esssecuritylabel>", payload.getItems().get(0).getLabel().getLabel());
		assertEquals(false, payload.getItems().get(0).getIsDefault());
		assertEquals("Classified|SECRET", payload.getItems().get(0).getSelector());
		assertEquals("CONFIDENTIAL", payload.getItems().get(1).getLabel().getDisplayMarking());
		assertEquals("<esssecuritylabel xmlns=\"urn:xmpp:sec-label:ess:0\">MQMGASk=</esssecuritylabel>", payload.getItems().get(1).getLabel().getLabel());
		assertEquals(true, payload.getItems().get(1).getIsDefault());
		assertEquals("Classified|CONFIDENTIAL", payload.getItems().get(1).getSelector());
		assertEquals(false, payload.getItems().get(2).getIsDefault());
		assertEquals("Unclassified|UNCLASSIFIED", payload.getItems().get(2).getSelector());
		assertNull(payload.getItems().get(2).getLabel());	
	}
}