/*
 * Copyright (c) 2013 Isode Limited.
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
import com.isode.stroke.parser.StreamManagementEnabledParser;
import com.isode.stroke.elements.StreamManagementEnabled;
import com.isode.stroke.parser.ElementParserTester;

public class StreamManagementEnabledParserTest {

	public StreamManagementEnabledParserTest() {

	}

	@Test
	public void testParse() {
		StreamManagementEnabledParser testling = new StreamManagementEnabledParser();
		ElementParserTester parser = new ElementParserTester(testling);

		assertTrue(parser.parse(
			"<enabled xmlns=\"urn:xmpp:sm:3\" id=\"some-long-sm-id\" resume=\"true\"/>"));

		StreamManagementEnabled element = (StreamManagementEnabled)(testling.getElement());
		assertTrue(element.getResumeSupported());
		assertEquals("some-long-sm-id", element.getResumeID());
	}
}