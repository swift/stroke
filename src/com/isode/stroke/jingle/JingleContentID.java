/*
 * Copyright (c) 2011-2015 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.jingle;

import com.isode.stroke.elements.JingleContentPayload;

public class JingleContentID {

	private String name = "";
	private JingleContentPayload.Creator creator;

	public JingleContentID(final String name, JingleContentPayload.Creator creator) {
		this.name = name;
		this.creator = creator;
	}
			
	public String getName() {
		return this.name;
	}
			
	public JingleContentPayload.Creator getCreator() {
		return this.creator;
	}
}