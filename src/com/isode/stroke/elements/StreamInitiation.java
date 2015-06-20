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
import com.isode.stroke.elements.StreamInitiationFileInfo;
import com.isode.stroke.base.NotNull;
import java.util.Vector;

public class StreamInitiation extends Payload {

	private boolean isFileTransfer;
	private String id = "";
	private StreamInitiationFileInfo fileInfo;
	private Vector<String> providedMethods = new Vector<String>();
	private String requestedMethod = "";

	/**
	* Default Constructor.
	*/
	public StreamInitiation() {
		this.isFileTransfer = true;
	}

	/**
	* @return id, NotNull.
	*/
	public String getID() {
		return id;
	}

	/**
	* @param id, NotNull.
	*/
	public void setID(String id) {
		NotNull.exceptIfNull(id, "id");
		this.id = id;
	}

	/**
	* @return fileInfo.
	*/
	public StreamInitiationFileInfo getFileInfo() {
		return fileInfo;
	}

	/**
	* @param fileInfo.
	*/
	public void setFileInfo(StreamInitiationFileInfo info) {
		fileInfo = info;
	}

	/**
	* @return providedMethods.
	*/
	public Vector<String> getProvidedMethods() {
		return providedMethods;
	}

	/**
	* @param method, Not Null.
	*/
	public void addProvidedMethod(String method) {
		NotNull.exceptIfNull(method, "method");
		providedMethods.add(method);
	}

	/**
	* @param method, Not Null.
	*/
	public void setRequestedMethod(String method) {
		NotNull.exceptIfNull(method, "method");
		requestedMethod = method;
	}

	/**
	* @return method, Not Null.
	*/
	public String getRequestedMethod() {
		return requestedMethod;
	}

	/**
	* @return isFileTransfer.
	*/
	public boolean getIsFileTransfer() {
		return isFileTransfer;
	}

	/**
	* @param isFileTransfer.
	*/
	public void setIsFileTransfer(boolean b) {
		isFileTransfer = b;
	}
}