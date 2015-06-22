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
import com.isode.stroke.parser.MessageParser;
import com.isode.stroke.elements.Message;

public class MessageParserTest {

	private PayloadParserFactoryCollection factoryCollection_;

	public MessageParserTest() {

	}

	@Before
	public void setUp() {
		factoryCollection_ = new PayloadParserFactoryCollection();
	}

	@Test
	public void testParse_Chat() {
		MessageParser testling = new MessageParser(factoryCollection_);
		StanzaParserTester parser = new StanzaParserTester(testling);

		assertTrue(parser.parse("<message type=\"chat\"/>"));

		assertEquals(Message.Type.Chat, testling.getStanzaGeneric().getType());
	}

	@Test
	public void testParse_Groupchat() {
		MessageParser testling = new MessageParser(factoryCollection_);
		StanzaParserTester parser = new StanzaParserTester(testling);

		assertTrue(parser.parse("<message type=\"groupchat\"/>"));

		assertEquals(Message.Type.Groupchat, testling.getStanzaGeneric().getType());
	}

	@Test
	public void testParse_Error() {
		MessageParser testling = new MessageParser(factoryCollection_);
		StanzaParserTester parser = new StanzaParserTester(testling);

		assertTrue(parser.parse("<message type=\"error\"/>"));

		assertEquals(Message.Type.Error, testling.getStanzaGeneric().getType());
	}

	@Test
	public void testParse_Headline() {
		MessageParser testling = new MessageParser(factoryCollection_);
		StanzaParserTester parser = new StanzaParserTester(testling);

		assertTrue(parser.parse("<message type=\"headline\"/>"));

		assertEquals(Message.Type.Headline, testling.getStanzaGeneric().getType());
	}

	@Test
	public void testParse_Normal() {
		MessageParser testling = new MessageParser(factoryCollection_);
		StanzaParserTester parser = new StanzaParserTester(testling);

		assertTrue(parser.parse("<message/>"));

		assertEquals(Message.Type.Normal, testling.getStanzaGeneric().getType());
	}

}