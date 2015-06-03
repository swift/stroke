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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;
import com.isode.stroke.elements.VCardUpdate;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.parser.payloadparsers.VCardUpdateParser;
import com.isode.stroke.parser.payloadparsers.PayloadsParserTester;
import com.isode.stroke.eventloop.DummyEventLoop;

public class VCardUpdateParserTest {

	@Test
	public void testParse() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertTrue(parser.parse("<x xmlns='vcard-temp:x:update'><photo>sha1-hash-of-image</photo></x>"));
		VCardUpdate payload = (VCardUpdate)parser.getPayload();
		assertEquals("sha1-hash-of-image", payload.getPhotoHash());
	}
}