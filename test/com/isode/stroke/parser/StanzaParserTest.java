/*
 * Copyright (c) 2010-2014 Isode Limited.
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
import com.isode.stroke.parser.StanzaParser;
import com.isode.stroke.parser.PayloadParserFactory;
import com.isode.stroke.parser.PayloadParserFactoryCollection;
import com.isode.stroke.elements.Stanza;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.elements.Element;
import com.isode.stroke.parser.GenericPayloadParser;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.jid.JID;

public class StanzaParserTest {

	public StanzaParserTest() {

	}

	private class MyPayload1 extends Payload {

		public MyPayload1() {
			this.hasChild = false; 
		}

		public boolean hasChild;
	}

	private class MyPayload1Parser extends GenericPayloadParser<MyPayload1> {
			
		public MyPayload1Parser() {
			super(new MyPayload1());
		}

		public void handleStartElement(String element, String ns, AttributeMap attributes) { 
			if (!element.equals("mypayload1")) {
				getPayloadInternal().hasChild = true;
			}
		}

		public void handleEndElement(String element, String ns) {}
		public void handleCharacterData(String data) {}
	}

	private class MyPayload1ParserFactory implements PayloadParserFactory {

		public MyPayload1ParserFactory() {

		}

		public PayloadParser createPayloadParser() { 
			return new MyPayload1Parser(); 
		}

		public boolean canParse(String element, String ns, AttributeMap attributes) {
				return element.equals("mypayload1");
		}
	}

	private class MyPayload2 extends Payload {

		public MyPayload2() {

		}
	}

	private class MyPayload2Parser extends GenericPayloadParser<MyPayload2> {
			
		public MyPayload2Parser() {
			super(new MyPayload2());
		}

		public void handleStartElement(String element, String ns, AttributeMap attributes) {}
		public void handleEndElement(String element, String ns) {}
		public void handleCharacterData(String data) {}
	}

	private class MyPayload2ParserFactory implements PayloadParserFactory {

		public MyPayload2ParserFactory() {

		}

		public PayloadParser createPayloadParser() { 
			return new MyPayload2Parser(); 
		}

		public boolean canParse(String element, String ns, AttributeMap attributes) {
			return element.equals("mypayload2");
		}
	}


	private class MyStanza extends Stanza {

		public MyStanza() {}
	}

	private class MyStanzaParser extends StanzaParser {

		public MyStanzaParser(PayloadParserFactoryCollection collection) {
			super(collection);
			stanza_ = new MyStanza();
		}

		public Element getElement() {
			return stanza_;
		}

		private MyStanza stanza_;
	}

	private MyPayload1ParserFactory factory1_ = new MyPayload1ParserFactory();
	private MyPayload2ParserFactory factory2_ = new MyPayload2ParserFactory();
	private PayloadParserFactoryCollection factoryCollection_;

	@Before
	public void setUp() {
		factoryCollection_ = new PayloadParserFactoryCollection();
		factoryCollection_.addFactory(factory1_);
		factoryCollection_.addFactory(factory2_);
	}

	@Test
	public void testHandleEndElement_OnePayload() {
		MyStanzaParser testling = new MyStanzaParser(factoryCollection_);

		AttributeMap attributes = new AttributeMap();
		attributes.addAttribute("foo", "", "fum");
		attributes.addAttribute("bar", "", "baz");
		testling.handleStartElement("mystanza", "", attributes);
		testling.handleStartElement("mypayload1", "", attributes);
		testling.handleStartElement("child", "", attributes);
		testling.handleEndElement("child", "");
		testling.handleEndElement("mypayload1", "");
		testling.handleEndElement("mystanza", "");

		assertNotNull(testling.getStanza().getPayload(new MyPayload1()));
		assertTrue(testling.getStanza().getPayload(new MyPayload1()).hasChild);
	}

	@Test
	public void testHandleEndElement_MultiplePayloads() {
		MyStanzaParser testling = new MyStanzaParser(factoryCollection_);

		AttributeMap attributes = new AttributeMap();
		testling.handleStartElement("mystanza", "", attributes);
		testling.handleStartElement("mypayload1", "", attributes);
		testling.handleEndElement("mypayload1", "");
		testling.handleStartElement("mypayload2", "", attributes);
		testling.handleEndElement("mypayload2", "");
		testling.handleEndElement("mystanza", "");

		assertNotNull(testling.getStanza().getPayload(new MyPayload1()));
		assertNotNull(testling.getStanza().getPayload(new MyPayload2()));
	}

	@Test
	public void testHandleEndElement_StrayCharacterData() {
		MyStanzaParser testling = new MyStanzaParser(factoryCollection_);

		AttributeMap attributes = new AttributeMap();
		testling.handleStartElement("mystanza", "", attributes);
		testling.handleStartElement("mypayload1", "", attributes);
		testling.handleEndElement("mypayload1", "");
		testling.handleCharacterData("bla");
		testling.handleStartElement("mypayload2", "", attributes);
		testling.handleEndElement("mypayload2", "");
		testling.handleEndElement("mystanza", "");

		assertNotNull(testling.getStanza().getPayload(new MyPayload1()));
		assertNotNull(testling.getStanza().getPayload(new MyPayload2()));
	}

	@Test
	public void testHandleEndElement_UnknownPayload() {
		MyStanzaParser testling = new MyStanzaParser(factoryCollection_);

		AttributeMap attributes = new AttributeMap();
		testling.handleStartElement("mystanza", "", attributes);
		testling.handleStartElement("mypayload1", "", attributes);
		testling.handleEndElement("mypayload1", "");
		testling.handleStartElement("unknown-payload", "", attributes);
		testling.handleStartElement("unknown-payload-child", "", attributes);
		testling.handleEndElement("unknown-payload-child", "");
		testling.handleEndElement("unknown-payload", "");
		testling.handleStartElement("mypayload2", "", attributes);
		testling.handleEndElement("mypayload2", "");
		testling.handleEndElement("mystanza", "");

		assertNotNull(testling.getStanza().getPayload(new MyPayload1()));
		assertNotNull(testling.getStanza().getPayload(new MyPayload2()));
	}

	@Test
	public void testHandleParse_BasicAttributes() {
		MyStanzaParser testling = new MyStanzaParser(factoryCollection_);

		AttributeMap attributes = new AttributeMap();
		attributes.addAttribute("to", "", "foo@example.com/blo");
		attributes.addAttribute("from", "", "bar@example.com/baz");
		attributes.addAttribute("id", "", "id-123");
		testling.handleStartElement("mystanza", "", attributes);
		testling.handleEndElement("mypayload1", "");

		assertEquals(new JID("foo@example.com/blo"), testling.getStanza().getTo());
		assertEquals(new JID("bar@example.com/baz"), testling.getStanza().getFrom());
		assertEquals("id-123", testling.getStanza().getID());
	}
}