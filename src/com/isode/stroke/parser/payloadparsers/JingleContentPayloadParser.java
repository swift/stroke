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

import com.isode.stroke.parser.GenericPayloadParser;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.PayloadParserFactoryCollection;
import com.isode.stroke.parser.PayloadParserFactory;
import com.isode.stroke.parser.PayloadParser;
import com.isode.stroke.elements.JingleContentPayload;
import com.isode.stroke.elements.JingleTransportPayload;
import com.isode.stroke.elements.JingleDescription;
import com.isode.stroke.base.NotNull;

public class JingleContentPayloadParser extends GenericPayloadParser<JingleContentPayload> {

	private PayloadParserFactoryCollection factories;
	private int level;
	private PayloadParser currentPayloadParser;

	public JingleContentPayloadParser(PayloadParserFactoryCollection factories) {
		super(new JingleContentPayload());
		this.factories = factories;
		this.level = 0;
	}

	/**
	* @param element, NotNull.
	* @param ns.
	* @param attributes, NotNull.
	*/
	@Override
	public void handleStartElement(String element, String ns, AttributeMap attributes) {
		NotNull.exceptIfNull(attributes, "attributes");
		if (level == 0) {
			String creator = "";
			if(attributes.getAttributeValue("creator") != null) {
				creator = attributes.getAttributeValue("creator");
			} else {
				creator = "";
			}
			if (creator.equals("initiator")) {
				getPayloadInternal().setCreator(JingleContentPayload.Creator.InitiatorCreator);
			} else if (creator.equals("responder")) {
				getPayloadInternal().setCreator(JingleContentPayload.Creator.ResponderCreator);
			} else {
				getPayloadInternal().setCreator(JingleContentPayload.Creator.UnknownCreator);
			}
			if(attributes.getAttributeValue("name") != null) {
				getPayloadInternal().setName(attributes.getAttributeValue("name"));
			} else {
				getPayloadInternal().setName("");	
			}
		}

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
	* @param element, NotNull.
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
				if(currentPayloadParser.getPayload() instanceof JingleTransportPayload) {
					JingleTransportPayload transport = (JingleTransportPayload)(currentPayloadParser.getPayload());
					if (transport != null) {
						getPayloadInternal().addTransport(transport);
					}
				}
				if(currentPayloadParser.getPayload() instanceof JingleDescription) {
					JingleDescription description = (JingleDescription)(currentPayloadParser.getPayload());
					if (description != null) {
						getPayloadInternal().addDescription(description);
					}
				}
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