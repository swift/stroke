/*
 * Copyright (c) 2011 Isode Limited.
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
import com.isode.stroke.elements.Payload;

public class JingleTransportPayload extends Payload {

	private String sessionID = "";

	/**
	* @param Id, Not Null.
	*/
	public void setSessionID(String id) {
		NotNull.exceptIfNull(id, "id");
		sessionID = id;
	}

	/**
	* @return Id, Not Null.
	*/
	public String getSessionID() {
		return sessionID;
	}  
}