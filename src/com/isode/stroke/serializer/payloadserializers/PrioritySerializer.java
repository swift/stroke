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

package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLTextNode;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.elements.Priority;
import com.isode.stroke.base.NotNull;

public class PrioritySerializer extends GenericPayloadSerializer<Priority> {

	public PrioritySerializer() {
		super(Priority.class);
	}

	public String serializePayload(Priority priority) {
		return "<priority>" + Integer.toString(priority.getPriority()) + "</priority>";
	}
}