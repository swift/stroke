/*
 * Copyright (c) 2010 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015, Tarun Gupta.
 * All rights reserved.
 */

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.parser.GenericPayloadParser;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.elements.CapsInfo;
import com.isode.stroke.base.NotNull;

public class CapsInfoParser extends GenericPayloadParser<CapsInfo> {

	private int level_ = 0;

	/**
	* CapsInfoParser();
	*/
	public CapsInfoParser() {
		super(new CapsInfo());
	}

	/**
	* @param attributes, notnull.
	*/
	public void handleStartElement(String element, String ns, AttributeMap attributes) {
		NotNull.exceptIfNull(attributes, "attributes");
		if (this.level_ == 0) {
			getPayloadInternal().setHash(attributes.getAttribute("hash"));
			getPayloadInternal().setNode(attributes.getAttribute("node"));
			getPayloadInternal().setVersion(attributes.getAttribute("ver"));
		}
		++level_;
	}

	public void handleEndElement(String element, String ns) {
		--level_;
	}

	public void handleCharacterData(String data) {

	}

}
