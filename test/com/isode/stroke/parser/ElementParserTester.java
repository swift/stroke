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

import com.isode.stroke.parser.XMLParser;
import com.isode.stroke.eventloop.EventLoop;
import com.isode.stroke.parser.XMLParserClient;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.ElementParser;
import com.isode.stroke.parser.PlatformXMLParserFactory;

public class ElementParserTester<ParserType extends ElementParser> implements XMLParserClient {

	private XMLParser xmlParser_;
	private ParserType parser_;

	public ElementParserTester(ParserType parser) {
		this.parser_ = parser;
		xmlParser_ = PlatformXMLParserFactory.createXMLParser(this);
	}

	public boolean parse(String data) {
		return xmlParser_.parse(data);
	}

	public void handleStartElement(String element, String ns, AttributeMap attributes) {
		parser_.handleStartElement(element, ns, attributes);
	}

	public void handleEndElement(String element, String ns) {
		parser_.handleEndElement(element, ns);
	}

	public void handleCharacterData(String data) {
		parser_.handleCharacterData(data);
	}
}

