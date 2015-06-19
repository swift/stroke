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
import com.isode.stroke.elements.Thread;
import com.isode.stroke.base.NotNull;

public class ThreadParser extends GenericPayloadParser<Thread> {

	private int level_ = 0;
	private String text_ = "";

	public ThreadParser() {
		super(new Thread());
	}

	/**
	* @param element, NotNull.
	* @param ns.
	* @param attributes, NotNull.
	*/
	@Override
	public void handleStartElement(String element, String ns, AttributeMap attributes) {
		NotNull.exceptIfNull(element, "element");
		NotNull.exceptIfNull(attributes, "attributes");
		++level_;
		if (element.equals("thread")) {
			if(attributes.getAttributeValue("parent") != null) {
				getPayloadInternal().setParent(attributes.getAttributeValue("parent"));
			} else {
				getPayloadInternal().setParent("");
			}
		}
	}

	/**
	* @param element, NotNull.
	* @param ns.
	*/
	@Override
	public void handleEndElement(String element, String ns) {
		--level_;
		if (level_ == 0) {
			getPayloadInternal().setText(text_);
		}
	}

	/**
	* @param data, NotNull.
	*/
	@Override
	public void handleCharacterData(String data) {
		NotNull.exceptIfNull(data, "data");
		if (level_ == 1) {
			text_ += data;
		}
	}
}