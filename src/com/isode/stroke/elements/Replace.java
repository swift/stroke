/*
 * Copyright (c) 2015, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2011 Vlad Voicu
 * Licensed under the Simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
package com.isode.stroke.elements;

public class Replace extends Payload {
	private String replaceID_ = "";
	
	public Replace() {
		this("");
	}
	
	public Replace(String id) {
		replaceID_ = id;
	}
	
	public String getID() {
		return replaceID_;
	}
	
	public void setID(String id) {
		replaceID_ = id;
	}

}
