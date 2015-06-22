/*
 * Copyright (c) 2011 Isode Limited.
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
import com.isode.stroke.parser.GenericPayloadTreeParser;
import com.isode.stroke.elements.RawXMLPayload;
import com.isode.stroke.parser.tree.ParserElement;
import com.isode.stroke.parser.tree.NullParserElement;
import com.isode.stroke.parser.payloadparsers.PayloadParserTester;

public class GenericPayloadTreeParserTest {

	public GenericPayloadTreeParserTest() {

	}

	private class MyParser extends GenericPayloadTreeParser<RawXMLPayload>	{

		public MyParser() {
			super(new RawXMLPayload());
		}

		public void handleTree(ParserElement root) {
			tree = root;
		}

		public ParserElement tree;
	}

	@Test
	public void testTree() {
		MyParser testling = new MyParser();

		String data = "<topLevel xmlns='urn:test:top'><firstLevelInheritedEmpty/><firstLevelInherited><secondLevelMultiChildren num='1'/><secondLevelMultiChildren num='2'/></firstLevelInherited><firstLevelNS xmlns='urn:test:first'/></topLevel>";

		PayloadParserTester tester = new PayloadParserTester(testling);
		tester.parse(data);

		ParserElement tree = testling.tree;

		assertEquals("topLevel", tree.getName());
		assertEquals("urn:test:top", tree.getNamespace());
		assertNotNull(tree.getChild("firstLevelInheritedEmpty", "urn:test:top"));
		assertTrue(tree.getChild("firstLevelInheritedEmpty", "") instanceof NullParserElement);
		assertNotNull(tree.getChild("firstLevelInherited", "urn:test:top"));
		assertEquals(2, tree.getChild("firstLevelInherited", "urn:test:top").getChildren("secondLevelMultiChildren", "urn:test:top").size());
		assertEquals("1", tree.getChild("firstLevelInherited", "urn:test:top").getChildren("secondLevelMultiChildren", "urn:test:top").get(0).getAttributes().getAttribute("num"));
		assertEquals("2", tree.getChild("firstLevelInherited", "urn:test:top").getChildren("secondLevelMultiChildren", "urn:test:top").get(1).getAttributes().getAttribute("num"));
		assertNotNull(tree.getChild("firstLevelNS", "urn:test:first"));
	}
}