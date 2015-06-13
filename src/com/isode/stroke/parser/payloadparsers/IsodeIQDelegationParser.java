/*
 * Copyright (c) 2014 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.parser.GenericPayloadParser;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.PayloadParserFactoryCollection;
import com.isode.stroke.parser.PayloadParser;
import com.isode.stroke.parser.PayloadParserFactory;
import com.isode.stroke.elements.IsodeIQDelegation;
import com.isode.stroke.elements.Forwarded;
import com.isode.stroke.base.NotNull;

public class IsodeIQDelegationParser extends GenericPayloadParser<IsodeIQDelegation> {

	private PayloadParserFactoryCollection parsers;
	private int level = 0;
	private PayloadParser currentPayloadParser;

	public IsodeIQDelegationParser(PayloadParserFactoryCollection parsers) {
		super(new IsodeIQDelegation());
		this.parsers = parsers;
		this.level = 0;
	}

	/**
	* @param element.
	* @param ns.
	* @param attributes.
	*/
	@Override
	public void handleStartElement(String element, String ns, AttributeMap attributes) {
		if (level == 1) {
			PayloadParserFactory factory = parsers.getPayloadParserFactory(element, ns, attributes);
			if (factory != null) {
				currentPayloadParser = factory.createPayloadParser();
			}
		}

		if (level >= 1 && currentPayloadParser != null) {
			currentPayloadParser.handleStartElement(element, ns, attributes);
		}
		++level;
	}

	/**
	* @param element.
	* @param ns.
	*/
	@Override
	public void handleEndElement(String element, String ns) {
		--level;
		if (currentPayloadParser != null) {
			if (level >= 1) {
				currentPayloadParser.handleEndElement(element, ns);
			}

			if (level == 1) {
				getPayloadInternal().setForward((Forwarded)(currentPayloadParser.getPayload()));
				currentPayloadParser = null;
			}
		}
	}

	/**
	* @param data, NotNull.
	*/
	@Override
	public void handleCharacterData(String data) {
		NotNull.exceptIfNull(data, "data");
		if (level > 1 && currentPayloadParser != null) {
			currentPayloadParser.handleCharacterData(data);
		}
	}
}