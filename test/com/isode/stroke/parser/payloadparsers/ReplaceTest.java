/*
 * Copyright (c) 2011 Vlad Voicu
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
import com.isode.stroke.elements.Replace;
import com.isode.stroke.parser.payloadparsers.ReplaceParser;
import com.isode.stroke.parser.payloadparsers.PayloadsParserTester;
import com.isode.stroke.eventloop.DummyEventLoop;

public class ReplaceTest {

	public ReplaceTest() {

	}

	@Test
	public void testParseTrivial() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse("<replace id='bad1' xmlns='http://swift.im/protocol/replace'/>"));
		Replace payload = (Replace)(parser.getPayload());
		assertEquals("bad1", payload.getID());
	}

	@Test
	public void testParseChild() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse("<replace id='bad1' xmlns='http://swift.im/protocol/replace' ><child xmlns='blah' id=\"hi\"/></replace>"));
		Replace payload = (Replace)(parser.getPayload());
		assertEquals("bad1", payload.getID());
	}
}