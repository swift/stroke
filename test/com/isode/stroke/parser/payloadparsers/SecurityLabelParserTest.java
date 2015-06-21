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
import com.isode.stroke.elements.SecurityLabel;
import com.isode.stroke.parser.payloadparsers.SecurityLabelParser;
import com.isode.stroke.parser.payloadparsers.PayloadsParserTester;
import com.isode.stroke.eventloop.DummyEventLoop;

public class SecurityLabelParserTest {

	public SecurityLabelParserTest() {

	}

	@Test
	public void testParse() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);

		assertNotNull(parser.parse(
			"<securitylabel xmlns=\"urn:xmpp:sec-label:0\">" +
				"<displaymarking fgcolor=\"black\" bgcolor=\"red\">SECRET</displaymarking>" +
				"<label>" +
					"<esssecuritylabel xmlns=\"urn:xmpp:sec-label:ess:0\">MQYCAQQGASk=</esssecuritylabel>" +
				"</label>" +
				"<equivalentlabel>" +
					"<icismlabel xmlns=\"http://example.gov/IC-ISM/0\" classification=\"S\" ownerProducer=\"USA\" disseminationControls=\"FOUO\"/>" +
				"</equivalentlabel>" +
				"<equivalentlabel>" +
					"<esssecuritylabel xmlns=\"urn:xmpp:sec-label:ess:0\">MRUCAgD9DA9BcXVhIChvYnNvbGV0ZSk=</esssecuritylabel>" +
				"</equivalentlabel>" +
			"</securitylabel>"));

		SecurityLabel payload = (SecurityLabel)(parser.getPayload());
		assertEquals("SECRET", payload.getDisplayMarking());
		assertEquals("black", payload.getForegroundColor());
		assertEquals("red", payload.getBackgroundColor());
		assertEquals("<esssecuritylabel xmlns=\"urn:xmpp:sec-label:ess:0\">MQYCAQQGASk=</esssecuritylabel>", payload.getLabel());	
		assertEquals("<icismlabel classification=\"S\" disseminationControls=\"FOUO\" ownerProducer=\"USA\" xmlns=\"http://example.gov/IC-ISM/0\"/>", payload.getEquivalentLabels().toArray()[0]);	
		assertEquals("<esssecuritylabel xmlns=\"urn:xmpp:sec-label:ess:0\">MRUCAgD9DA9BcXVhIChvYnNvbGV0ZSk=</esssecuritylabel>", payload.getEquivalentLabels().toArray()[1]);	
	}
}