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

