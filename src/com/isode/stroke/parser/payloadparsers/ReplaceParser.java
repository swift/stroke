/*
 * Copyright (c) 2011 Vlad Voicu
 * Licensed under the Simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
/*
 * Copyright (c) 2015 Thomas Graviou
 * Licensed under the Simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.Replace;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;
import com.isode.stroke.parser.PayloadParser;
import com.isode.stroke.parser.PayloadParserFactory;
import com.isode.stroke.parser.PayloadParserFactoryCollection;

/**
 * Class that represents a parser for the Error payload
 *
 */
public class ReplaceParser extends GenericPayloadParser<Replace> {
	
	public ReplaceParser() {
		super(new Replace());
	}
	
	@Override
	public void handleStartElement(String element, String ns, AttributeMap attributes) {
		if(level_ == 0) {
			String id = attributes.getAttribute("id");
			getPayloadInternal().setID(id);
		}
		level_++;
	}
	
	@Override
	public void handleEndElement(String element, String ns) {
		--level_;
	}
	
	@Override
	public void handleCharacterData(String data) {	
	}
	
	private int level_ = 0;
}
