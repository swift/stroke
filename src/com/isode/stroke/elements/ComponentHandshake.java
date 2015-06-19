/*
 * Copyright (c) 2010-2014 Isode Limited.
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
import com.isode.stroke.elements.TopLevelElement;

public class ComponentHandshake implements TopLevelElement {

	private String data = "";

	/**
	* Default Constructor.
	*/
	public ComponentHandshake() {
		this("");
	}

	/**
	* Parameterized Constructor.
	* @param data, Not Null.
	*/
	public ComponentHandshake(String data) {
		NotNull.exceptIfNull(data, "data");
		this.data = data;
	}

	/**
	* @return data, NotNull.
	*/
	public String getData() {
		return data;
	}

	/**
	* @param data, NotNull.
	*/
	public void setData(String data) {
		NotNull.exceptIfNull(data, "data");
		this.data = data;
	}
}