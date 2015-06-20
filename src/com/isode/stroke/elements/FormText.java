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

import com.isode.stroke.base.NotNull;

public class FormText {

	private String text_ = "";

	/**
	* Default Constructor.
	*/
	public FormText() {

	}

	/**
	* @param text, Not Null.
	*/
	public void setTextString(String text) {
		NotNull.exceptIfNull(text, "text");
		text_ = text;
	}

	/**
	* @return text, Not Null.
	*/	
	public String getTextString() {
		return text_;
	}
}