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
import com.isode.stroke.elements.Status;
import com.isode.stroke.parser.payloadparsers.StatusParser;
import com.isode.stroke.parser.payloadparsers.PayloadsParserTester;
import com.isode.stroke.eventloop.DummyEventLoop;

public class StatusParserTest {

	public StatusParserTest() {

	}

	@Test
	public void testParse() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse("<status>foo<baz>bar</baz>fum</status>"));

		Status payload = (Status)(parser.getPayload());
		assertEquals("foobarfum", payload.getText());
	}
}