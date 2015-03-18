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

import com.isode.stroke.parser.PayloadParserFactory;
import com.isode.stroke.parser.payloadparsers.ChatStateParser;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.PayloadParser;

public class ChatStateParserFactory implements PayloadParserFactory {

	/**
	* ChatStateParserFactory();
	*/
	public ChatStateParserFactory() {

	}

	/**
	* @param attributes, notnull
	*/
	public boolean canParse(String element, String ns, AttributeMap attributes) {
		return  ((ns == "http://jabber.org/protocol/chatstates") && 
			(element == "active" || element == "composing" || element == "paused" || element == "inactive" || element == "gone"));
	}

	/**
	* @return PayloadParser()
	*/
	public PayloadParser createPayloadParser() {
		return (new ChatStateParser());
	}
}

