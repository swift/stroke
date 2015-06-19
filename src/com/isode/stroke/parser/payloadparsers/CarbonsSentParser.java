/*
 * Copyright (c) 2015 Isode Limited.
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
import com.isode.stroke.parser.PayloadParserFactoryCollection;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.payloadparsers.ForwardedParser;
import com.isode.stroke.elements.CarbonsSent;
import com.isode.stroke.base.NotNull;

public class CarbonsSentParser extends GenericPayloadParser<CarbonsSent> {

	private final int TopLevel = 0;
	private final int PayloadLevel = 1;
	private int level_ = TopLevel;
	private PayloadParserFactoryCollection factories_;
	private ForwardedParser forwardedParser_;

	public CarbonsSentParser(PayloadParserFactoryCollection factories) {
		super(new CarbonsSent());
		this.factories_ = factories;
		this.level_ = TopLevel;
	}

	/**
	* @param element, NotNull.
	* @param ns.
	* @param attributes.
	*/
	@Override
	public void handleStartElement(String element, String ns, AttributeMap attributes) {
		NotNull.exceptIfNull(element, "element");
		if (level_ == PayloadLevel) {
			if (element.equals("forwarded")) {
				forwardedParser_ = (ForwardedParser)(new ForwardedParser(factories_));
			}
		}
		if (forwardedParser_ != null) {
			forwardedParser_.handleStartElement(element, ns, attributes);
		}
		++level_;
	}

	/**
	* @param element.
	* @param ns.
	*/
	@Override
	public void handleEndElement(String element, String ns) {
		--level_;
		if (forwardedParser_ != null && level_ >= PayloadLevel) {
			forwardedParser_.handleEndElement(element, ns);
		}
		if (forwardedParser_ != null && level_ == PayloadLevel) {
			/* done parsing nested stanza */
			getPayloadInternal().setForwarded(forwardedParser_.getPayloadInternal());
			forwardedParser_ = null;
		}
	}

	/**
	* @param data.
	*/
	@Override
	public void handleCharacterData(String data) {
		if (forwardedParser_ != null) {
			forwardedParser_.handleCharacterData(data);
		}
	}
}