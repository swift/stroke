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

import com.isode.stroke.parser.GenericElementParser;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.elements.ComponentHandshake;
import com.isode.stroke.base.NotNull;

public class ComponentHandshakeParser extends GenericElementParser<ComponentHandshake> {

	private int depth = 0;
	private String text = "";

	public ComponentHandshakeParser() {
		super(ComponentHandshake.class);
	}

	/**
	* @param element.
	* @param ns.
	* @param attributes.
	*/
	public void handleStartElement(String element, String ns, AttributeMap attributes) {
		++depth;
	}

	/**
	* @param element.
	* @param ns.
	*/
	public void handleEndElement(String element, String ns) {
		--depth;
		if (depth == 0) {
			getElementGeneric().setData(text);
		}
	}

	/**
	* @param text, NotNull.
	*/
	public void handleCharacterData(String text) {
		NotNull.exceptIfNull(text, "text");
		this.text += text;
	}
}