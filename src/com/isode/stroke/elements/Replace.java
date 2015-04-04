/*
 * Copyright (c) 2011 Vlad Voicu
 * Licensed under the Simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
 /*
 * Copyright (c) 2015 Thomas Graviou
 * Licensed under the Simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
 package com.isode.stroke.elements;
 
 import com.isode.stroke.base.NotNull;
 
 public class Replace extends Payload {
	 
	public Replace(String id) throws NullPointerException{
		NotNull.exceptIfNull(id, "id");
		replaceID_ = id;
	}
	 
	public Replace() {
		replaceID_ = new String();
	}
	 
	 public String getID() {
		 return replaceID_;
	 }
	 
	 public void setID(String id) throws NullPointerException{
		 NotNull.exceptIfNull(id, "id");
		 replaceID_ = id;
	 }
	 
	 private String replaceID_;
 }
 
