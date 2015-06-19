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
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.elements.CarbonsPrivate;
import com.isode.stroke.base.NotNull;

public class CarbonsPrivateParser extends GenericPayloadParser<CarbonsPrivate> {

	public CarbonsPrivateParser() {
		super(new CarbonsPrivate());
	}

	/**
	* @param element.
	* @param ns.
	* @param attributes.
	*/
	@Override
	public void handleStartElement(String element, String ns, AttributeMap attributes) {

	}

	/**
	* @param element.
	* @param ns.
	*/
	@Override
	public void handleEndElement(String element, String ns) {

	}

	/**
	* @param data.
	*/
	@Override
	public void handleCharacterData(String data) {

	}
}