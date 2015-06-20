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
import com.isode.stroke.elements.StreamInitiation;
import com.isode.stroke.elements.StreamInitiationFileInfo;
import com.isode.stroke.parser.payloadparsers.StreamInitiationParser;
import com.isode.stroke.parser.payloadparsers.PayloadsParserTester;
import com.isode.stroke.eventloop.DummyEventLoop;

public class StreamInitiationParserTest {

	public StreamInitiationParserTest() {

	}

	@Test
	public void testParse_Request() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse("<si xmlns='http://jabber.org/protocol/si' id='a0' mime-type='text/plain' profile='http://jabber.org/protocol/si/profile/file-transfer'>" +
						"<file xmlns='http://jabber.org/protocol/si/profile/file-transfer' name='test.txt' size='1022'>" +
					    "<desc>This is info about the file.</desc>" +
						"</file>" +
						"<feature xmlns='http://jabber.org/protocol/feature-neg'>" +
							"<x xmlns='jabber:x:data' type='form'>" +
								"<field var='stream-method' type='list-single'>" +
									"<option><value>http://jabber.org/protocol/bytestreams</value></option>" +
									"<option><value>jabber:iq:oob</value></option>" +
									"<option><value>http://jabber.org/protocol/ibb</value></option>" +
								"</field>" +
							"</x>" +
						"</feature>" +
					"</si>"));

		StreamInitiation si = (StreamInitiation)parser.getPayload();
		assertTrue(si.getIsFileTransfer());
		assertNotNull(si.getFileInfo());
		assertEquals("test.txt", si.getFileInfo().getName());
		assertEquals(1022L, si.getFileInfo().getSize());
		assertEquals("This is info about the file.", si.getFileInfo().getDescription());
		assertEquals(3, si.getProvidedMethods().size());
		assertEquals("http://jabber.org/protocol/bytestreams", si.getProvidedMethods().get(0));
		assertEquals("jabber:iq:oob", si.getProvidedMethods().get(1));
		assertEquals("http://jabber.org/protocol/ibb", si.getProvidedMethods().get(2));
	}

	@Test
	public void testParse_Response() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse("<si xmlns='http://jabber.org/protocol/si'>" +
						"<feature xmlns='http://jabber.org/protocol/feature-neg'>" +
							"<x xmlns='jabber:x:data' type='submit'>" +
								"<field var='stream-method'>" +
									"<value>http://jabber.org/protocol/bytestreams</value>" +
								"</field>" +
							"</x>" +
						"</feature>" +
					"</si>"));

		StreamInitiation si = (StreamInitiation)parser.getPayload();
		assertTrue(si.getIsFileTransfer());
		assertEquals("http://jabber.org/protocol/bytestreams", si.getRequestedMethod());
	}
}