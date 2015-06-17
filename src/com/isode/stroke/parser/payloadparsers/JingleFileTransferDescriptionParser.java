/*
 * Copyright (c) 2011 Tobias Markmann
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
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
import com.isode.stroke.elements.JingleFileTransferDescription;
import com.isode.stroke.elements.JingleFileTransferFileInfo;
import com.isode.stroke.base.NotNull;

public class JingleFileTransferDescriptionParser extends GenericPayloadParser<JingleFileTransferDescription> {

	private PayloadParserFactoryCollection factories;
	private int level = 0;
	private PayloadParser currentPayloadParser;

	public JingleFileTransferDescriptionParser(PayloadParserFactoryCollection factories) {
		super(new JingleFileTransferDescription());
		this.factories = factories;
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
			PayloadParserFactory payloadParserFactory = factories.getPayloadParserFactory(element, ns, attributes);
			if (payloadParserFactory != null) {
				currentPayloadParser = payloadParserFactory.createPayloadParser();
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
		if (level >= 1 && currentPayloadParser != null) {
			currentPayloadParser.handleEndElement(element, ns);
		}

		if (level == 0) {
			JingleFileTransferFileInfo info = (JingleFileTransferFileInfo)(currentPayloadParser.getPayload());
			if (info != null) {
				getPayloadInternal().setFileInfo(info);
			}
		}
	}

	/**
	* @param data, NotNull.
	*/
	@Override
	public void handleCharacterData(String data) {
		if (level >= 1 && currentPayloadParser != null) {
			currentPayloadParser.handleCharacterData(data);
		}
	}
}