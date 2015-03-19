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

package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.elements.ChatState;
import com.isode.stroke.base.NotNull;

public class ChatStateSerializer extends GenericPayloadSerializer<ChatState> {

	/**
	* CapsInfoSerializer();
	*/
	public ChatStateSerializer() {
		super(ChatState.class);
	}

	/**
	* @param chatState, notnull
	*/
	@Override
	protected String serializePayload(ChatState chatState) {
		NotNull.exceptIfNull(chatState, "chatState");
		String result = "<";
		ChatState.ChatStateType state = chatState.getChatState();
		if (state == ChatState.ChatStateType.Active) {
			result = result.concat("active");
		} else if (state == ChatState.ChatStateType.Composing) {
			result = result.concat("composing");
		} else if (state == ChatState.ChatStateType.Paused) {
			result = result.concat("paused");
		} else if (state == ChatState.ChatStateType.Inactive) {
			result = result.concat("inactive");	
		} else if (state == ChatState.ChatStateType.Gone) {
			result = result.concat("gone");
		}
		result = result.concat(" xmlns=\"http://jabber.org/protocol/chatstates\"/>");
		return result;
	}
} 