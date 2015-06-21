/*
 * Copyright (c) 2010-2015 Isode Limited.
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
import com.isode.stroke.elements.Priority;
import com.isode.stroke.base.NotNull;

public class PriorityParser extends GenericPayloadParser<Priority> {

	private int level_ = 0;
	private String text_ = "";

	public PriorityParser() {
		super(new Priority());
		this.level_ = 0;
	}

	/**
	* @param element, NotNull.
	* @param ns.
	* @param attributes.
	*/
	@Override
	public void handleStartElement(String element, String ns, AttributeMap attributes) {
		++level_;
	}

	/**
	* @param element, NotNull.
	* @param ns.
	*/
	@Override
	public void handleEndElement(String element, String ns) {
		--level_;
		if (level_ == 0) {
			int priority = 0;
			try {
				priority = Integer.parseInt(text_);
			}
			catch (NumberFormatException e) {

			}
			getPayloadInternal().setPriority(priority);
		}
	}

	/**
	* @param data, NotNull.
	*/
	@Override
	public void handleCharacterData(String data) {
		NotNull.exceptIfNull(data, "data");
		text_ += data;
	}
}