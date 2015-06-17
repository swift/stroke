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

package com.isode.stroke.elements;

import com.isode.stroke.elements.Payload;
import com.isode.stroke.base.NotNull;

public class Nickname extends Payload {

	private String nickname;

	/**
	* Default Constructor.
	*/
	public Nickname() {
		this("");
	}

	/**
	* Parameterized Constructor.
	* @param nickname, Not Null.
	*/
	public Nickname(String nickname) {
		NotNull.exceptIfNull(nickname, "nickname");
		this.nickname = nickname;
	}

	/**
	* @param nickname, Not Null.
	*/
	public void setNickname(String nickname) {
		NotNull.exceptIfNull(nickname, "nickname");
		this.nickname = nickname;
	}

	/**
	* @return nickname, Not Null.
	*/
	public String getNickname() {
		return nickname;
	}
}