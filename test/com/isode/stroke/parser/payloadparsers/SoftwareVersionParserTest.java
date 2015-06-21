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
import com.isode.stroke.elements.SoftwareVersion;
import com.isode.stroke.parser.payloadparsers.SoftwareVersionParser;
import com.isode.stroke.parser.payloadparsers.PayloadsParserTester;
import com.isode.stroke.eventloop.DummyEventLoop;

public class SoftwareVersionParserTest {

	public SoftwareVersionParserTest() {

	}

	@Test
	public void testParse() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse("<query xmlns=\"jabber:iq:version\">"
			+		"<name>myclient</name>"
			+		"<version>1.0</version>"
			+		"<os>Mac OS X</os>"
			+	"</query>"));

		SoftwareVersion payload = (SoftwareVersion)parser.getPayload();
		assertEquals("myclient", payload.getName());
		assertEquals("1.0", payload.getVersion());
		assertEquals("Mac OS X", payload.getOS());
	}
}