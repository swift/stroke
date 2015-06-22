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

package com.isode.stroke.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import com.isode.stroke.parser.SerializingParser;
import com.isode.stroke.parser.XMLParserClient;
import com.isode.stroke.parser.ParserTester;
import com.isode.stroke.parser.AttributeMap;

public class SerializingParserTest {

	private class ToTestSerializingParser extends SerializingParser implements XMLParserClient {

	}

	public SerializingParserTest() {

	}

	@Test
	public void testParse() {
		ToTestSerializingParser testling = new ToTestSerializingParser();
		ParserTester parser = new ParserTester(testling);

		assertTrue(parser.parse(
			"<message type=\"chat\" to=\"me@foo.com\">"
		+		"<body>Hello&lt;&amp;World</body>"
		+		"<html xmlns=\"http://www.w3.org/1999/xhtml\">"
		+			"foo<b>bar</b>baz"
		+		"</html>"
		+	"</message>"));

		assertEquals(
			"<message to=\"me@foo.com\" type=\"chat\">"
		+		"<body>Hello&lt;&amp;World</body>"
		+		"<html xmlns=\"http://www.w3.org/1999/xhtml\">foo<b xmlns=\"http://www.w3.org/1999/xhtml\">bar</b>baz</html>"
		+	"</message>", testling.getResult());
	}

	@Test
	public void testParse_Empty() {
		ToTestSerializingParser testling = new ToTestSerializingParser();

		assertEquals("", testling.getResult());
	}

	@Test
	public void testParse_ToplevelCharacterData() {
		ToTestSerializingParser testling = new ToTestSerializingParser();
			
		AttributeMap attributes = new AttributeMap();
		testling.handleCharacterData("foo");
		testling.handleStartElement("message", "", attributes);
		testling.handleEndElement("message", "");
		testling.handleCharacterData("bar");

		assertEquals("<message/>", testling.getResult());
	}	
}