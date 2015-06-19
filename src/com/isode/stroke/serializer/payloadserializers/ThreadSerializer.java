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

package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLTextNode;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.elements.Thread;
import com.isode.stroke.base.NotNull;

public class ThreadSerializer extends GenericPayloadSerializer<Thread> {

	public ThreadSerializer() {
		super(Thread.class);
	}

	public String serializePayload(Thread thread) {
		XMLElement threadNode = new XMLElement("thread", "", thread.getText());
		if (thread.getParent().length() != 0) {
			threadNode.setAttribute("parent", thread.getParent());
		}
		return threadNode.serialize();
	}
}