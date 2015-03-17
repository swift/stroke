/*
 * Copyright (c) 2010 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015, Tarun Gupta.
 * All rights reserved.
 */

package com.isode.stroke.elements;

import com.isode.stroke.base.NotNull;
import com.isode.stroke.elements.Payload;

public class ChatState extends Payload {

	public enum ChatStateType {Active, Composing, Paused, Inactive, Gone};

	private ChatStateType state_;
	
	/**
	* ChatState();
	*/
	public ChatState() {
		state_ = ChatStateType.Active;
	}

	/**
	* ChatState(state);
	*/
	public ChatState(ChatStateType state) {
		NotNull.exceptIfNull(state, "state");
		state_ = state;
	}

	/**
	*
	* @return state, notnull.
	*/
	public ChatStateType getChatState() {
		return state_;
	}

	/**
	*
	* @param state, notnull.
	*/
	public void setChatState(ChatStateType state) {
		NotNull.exceptIfNull(state, "state");
		state_ = state;
	}
}