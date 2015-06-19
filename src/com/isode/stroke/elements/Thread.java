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

package com.isode.stroke.elements;

import com.isode.stroke.elements.Payload;
import com.isode.stroke.base.NotNull;

public class Thread extends Payload {

	private String text_ = "";
	private String parent_ = "";

	/**
	* Default Constructor.
	*/
	public Thread() {
		this("", "");
	}

	/**
	* Parameterized Constructor.
	* @param text, Not Null.
	*/
	public Thread(String text) {
		this(text, "");
	}

	/**
	* Parameterized Constructor.
	* @param text, Not Null.
	* @param parent, not Null.
	*/
	public Thread(String text, String parent) {
		NotNull.exceptIfNull(text, "text");
		NotNull.exceptIfNull(parent, "parent");
		this.text_ = text;
		this.parent_ = parent;
	}

	/**
	* @param text, not Null.
	*/
	public void setText(String text) {
		NotNull.exceptIfNull(text, "text");
		text_ = text;
	}

	/**
	* @return text, not Null.
	*/
	public String getText() {
		return text_;
	}

	/**
	* @param parent, not Null.
	*/
	public void setParent(String parent) {
		NotNull.exceptIfNull(parent, "parent");
		parent_ = parent;
	}

	/**
	* @return parent, not Null.
	*/
	public String getParent() {
		return parent_;
	}
}