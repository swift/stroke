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
import com.isode.stroke.elements.IBB;
import com.isode.stroke.base.ByteArray;
import com.isode.stroke.parser.payloadparsers.IBBParser;
import com.isode.stroke.parser.payloadparsers.PayloadsParserTester;
import com.isode.stroke.eventloop.DummyEventLoop;

public class IBBParserTest {

	public IBBParserTest() {

	}

	@Test
	public void testParse_Data() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse("<data xmlns='http://jabber.org/protocol/ibb' seq='4'>\n" +
					"\t  YWJjZGVmZ2loamtsbW5vcHFyc3R1dnd4eXpBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWjEyMzQ1\n" +
					"\t  Njc4OTAK\n" +
					"</data>"));

		IBB ibb = (IBB)parser.getPayload();
		assertEquals(ibb.getAction(), IBB.Action.Data);
		assertEquals(new ByteArray("abcdefgihjklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890\n"), ibb.getData());
		assertEquals(4, ibb.getSequenceNumber());
	}
}