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

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.parser.GenericPayloadParser;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.elements.VCardUpdate;
import com.isode.stroke.base.NotNull;

public class VCardUpdateParser extends GenericPayloadParser<VCardUpdate> {

	private final int TopLevel = 0;
	private final int PayloadLevel = 1;
	private int level_ = TopLevel;
	private String currentText_ = new String();


	/**
	* Default Constructor.
	*/
	public VCardUpdateParser() {
		super(new VCardUpdate());
	}

	/**
	* @param element, notnull.
	*/
	public void handleStartElement(String element, String ns, AttributeMap attributes) {
		NotNull.exceptIfNull(element, "element");
		if (level_ == PayloadLevel) {
			currentText_ = "";
		}
		++level_;
	}

	/**
	* @param element, notnull.
	*/
	public void handleEndElement(String element, String ns) {
		NotNull.exceptIfNull(element, "element");
		--level_;
		if (level_ == PayloadLevel && element.equals("photo")) {
			getPayloadInternal().setPhotoHash(currentText_);
		}
	}

	/**
	* @param text, notnull.
	*/
	public void handleCharacterData(String text) {
		NotNull.exceptIfNull(text, "text");
		currentText_ += text;
	}

}