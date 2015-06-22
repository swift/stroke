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

package com.isode.stroke.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Before;
import com.isode.stroke.parser.PayloadParserFactoryCollection;
import com.isode.stroke.parser.StanzaParserTester;
import com.isode.stroke.parser.IQParser;
import com.isode.stroke.elements.IQ;

public class IQParserTest {

	private PayloadParserFactoryCollection factoryCollection_;

	public IQParserTest() {

	}

	@Before
	public void setUp() {
		factoryCollection_ = new PayloadParserFactoryCollection();
	}

	@Test
	public void testParse_Set() {
		IQParser testling = new IQParser(factoryCollection_);
		StanzaParserTester parser = new StanzaParserTester(testling);

		assertTrue(parser.parse("<iq type=\"set\"/>"));

		assertEquals(IQ.Type.Set, testling.getStanzaGeneric().getType());
	}

	@Test
	public void testParse_Get() {
		IQParser testling = new IQParser(factoryCollection_);
		StanzaParserTester parser = new StanzaParserTester(testling);

		assertTrue(parser.parse("<iq type=\"get\"/>"));

		assertEquals(IQ.Type.Get, testling.getStanzaGeneric().getType());
	}

	@Test
	public void testParse_Result() {
		IQParser testling = new IQParser(factoryCollection_);
		StanzaParserTester parser = new StanzaParserTester(testling);

		assertTrue(parser.parse("<iq type=\"result\"/>"));

		assertEquals(IQ.Type.Result, testling.getStanzaGeneric().getType());
	}

	@Test
	public void testParse_Error() {
		IQParser testling = new IQParser(factoryCollection_);
		StanzaParserTester parser = new StanzaParserTester(testling);

		assertTrue(parser.parse("<iq type=\"error\"/>"));

		assertEquals(IQ.Type.Error, testling.getStanzaGeneric().getType());
	}

}