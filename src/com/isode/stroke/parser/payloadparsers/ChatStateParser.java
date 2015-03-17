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
import com.isode.stroke.elements.ChatState;
import com.isode.stroke.base.NotNull;

public class ChatStateParser extends GenericPayloadParser<ChatState> {

	private int level_ = 0;

	/**
	* ChatStateParser();
	*/
	public ChatStateParser() {
		super(new ChatState());
	}

	/**
	* @param attributes, notnull.
	*/
	public void handleStartElement(String element, String ns, AttributeMap attributes) {
		NotNull.exceptIfNull(element, "element");
		if (this.level_ == 0) {
			ChatState.ChatStateType state = ChatState.ChatStateType.Active;
			if (element.equals("active")) {
				state = ChatState.ChatStateType.Active;
			} else if (element.equals("composing")) {
				state = ChatState.ChatStateType.Composing;
			} else if (element.equals("inactive")) {
				state = ChatState.ChatStateType.Inactive;
			} else if (element.equals("paused")) {
				state = ChatState.ChatStateType.Paused;
			} else if (element.equals("gone")) {
				state = ChatState.ChatStateType.Gone;
			}
			getPayloadInternal().setChatState(state);
		}
		++level_;
	}

	public void handleEndElement(String element, String ns) {
		--level_;
	}

	public void handleCharacterData(String data) {

	}

}