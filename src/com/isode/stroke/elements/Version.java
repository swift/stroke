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

public class Version extends Payload {

	private String name_;
	private String version_;
	private String os_;

	/**
	* Default Constructor.
	*/
	public Version() {
		this("", "", "");
	}

	/**
	* Parameterized Constructor.
	* @param name, Not Null.
	*/
	public Version(String name) {
		this(name, "", "");
	}

	/**
	* Parameterized Constructor.
	* @param name, Not Null.
	* @param version, NotNull.
	*/
	public Version(String name, String version) {
		this(name, version, "");
	}

	/**
	* Parameterized Constructor.
	* @param name, Not Null.
	* @param version, NotNull.
	* @param os , Not Null.
	*/
	public Version(String name, String version, String os) {
		NotNull.exceptIfNull(name, "name");
		NotNull.exceptIfNull(version, "version");
		NotNull.exceptIfNull(os, "os");
		this.name_ = name;
		this.version_ = version;
		this.os_ = os;
	}

	/**
	* @return name, Not Null.
	*/
	public String getName() {
		return name_;
	}

	/**
	* @return version, Not Null.
	*/
	public String getVersion() {
		return version_;
	}

	/**
	* @return os, Not Null.
	*/
	public String getOS() {
		return os_;
	}
}