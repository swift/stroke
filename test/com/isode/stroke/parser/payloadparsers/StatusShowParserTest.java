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
import com.isode.stroke.elements.StatusShow;
import com.isode.stroke.parser.payloadparsers.StatusShowParser;
import com.isode.stroke.parser.payloadparsers.PayloadsParserTester;
import com.isode.stroke.eventloop.DummyEventLoop;

public class StatusShowParserTest {

	public StatusShowParserTest() {

	}

	@Test
	public void testParse_Invalid() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse("<show>invalid</show>"));

		StatusShow payload = (StatusShow)(parser.getPayload());
		assertEquals(StatusShow.Type.Online, payload.getType());
	}

	@Test
	public void testParse_Away() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse("<show>away</show>"));

		StatusShow payload = (StatusShow)(parser.getPayload());
		assertEquals(StatusShow.Type.Away, payload.getType());
	}

	@Test
	public void testParse_FFC() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse("<show>chat</show>"));

		StatusShow payload = (StatusShow)(parser.getPayload());
		assertEquals(StatusShow.Type.FFC, payload.getType());
	}

	@Test
	public void testParse_XA() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse("<show>xa</show>"));

		StatusShow payload = (StatusShow)(parser.getPayload());
		assertEquals(StatusShow.Type.XA, payload.getType());
	}

	@Test
	public void testParse_DND() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse("<show>dnd</show>"));

		StatusShow payload = (StatusShow)(parser.getPayload());
		assertEquals(StatusShow.Type.DND, payload.getType());
	}
}