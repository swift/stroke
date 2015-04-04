/*
 * Copyright (c) 2011 Vlad Voicu
 * Licensed under the Simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
 /*
 * Copyright (c) 2015 Thomas Graviou
 * Licensed under the Simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.Replace;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.eventloop.DummyEventLoop;
import static org.junit.Assert.*;

import org.junit.Test;

public class ReplaceParserTest {

	private static Replace parse(String xmlString) {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertTrue(parser.parse(xmlString));
	
		Payload payload = null;
		do {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
			eventLoop.processEvents();
			payload = parser.getPayload();
		} while (payload == null);
		return (Replace) payload;
	}
	
	@Test
	public void testParseTrivial() {
		Replace payload = parse("<replace id='bad1' xmlns='http://swift.im/protocol/replace'/>");
		assertEquals("bad1", payload.getID());
	}
	
	@Test
	public void testParseChild() {
		Replace payload = parse("<replace id='bad1' xmlns='http://swift.im/protocol/replace' ><child xmlns='blah' id=\"hi\"/></replace>");
		assertEquals("bad1", payload.getID());
	}
	
	
}


