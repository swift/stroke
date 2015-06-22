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
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import com.isode.stroke.elements.RawXMLPayload;
import com.isode.stroke.parser.payloadparsers.RawXMLPayloadParser;
import com.isode.stroke.parser.payloadparsers.PayloadParserTester;
import com.isode.stroke.eventloop.DummyEventLoop;

public class RawXMLPayloadParserTest {

	public RawXMLPayloadParserTest() {

	}

	@Test
	public void testParse() {
		RawXMLPayloadParser testling = new RawXMLPayloadParser();
		PayloadParserTester parser = new PayloadParserTester(testling);

		String xml = 
			"<foo foo-attr=\"foo-val\" xmlns=\"ns:foo\">"
		+		"<bar bar-attr=\"bar-val\" xmlns=\"ns:bar\"/>"
		+		"<baz baz-attr=\"baz-val\" xmlns=\"ns:baz\"/>"
		+	"</foo>";
		assertTrue(parser.parse(xml));

		RawXMLPayload payload = (RawXMLPayload)(testling.getPayload());
		assertNotNull(xml, payload.getRawXML());
	}
}