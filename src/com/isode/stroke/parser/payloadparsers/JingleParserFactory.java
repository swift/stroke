/*
 * Copyright (c) 2011 Tobias Markmann
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.parser.GenericPayloadParserFactory;
import com.isode.stroke.parser.PayloadParserFactoryCollection;
import com.isode.stroke.parser.payloadparsers.JingleParser;
import com.isode.stroke.parser.PayloadParserFactory;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.PayloadParser;
import com.isode.stroke.base.NotNull;

public class JingleParserFactory implements PayloadParserFactory {

	private PayloadParserFactoryCollection factories;

	/**
	* JingleParserFactory();
	*/
	public JingleParserFactory(PayloadParserFactoryCollection factories) {
		this.factories = factories;
	}

	/**
	* @param attributes.
	* @param element, Not Null.
	* @param attributes, Not NUll.
	*/
	public boolean canParse(String element, String ns, AttributeMap attributes) {
		NotNull.exceptIfNull(element, "element");
		NotNull.exceptIfNull(ns, "ns");
		return element.equals("jingle") && ns.equals("urn:xmpp:jingle:1");
	}

	/**
	* @return PayloadParser()
	*/
	public PayloadParser createPayloadParser() {
		return new JingleParser(factories);
	}
}

