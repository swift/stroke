/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.Status;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.serializer.xml.XMLTextNode;

public class StatusSerializer extends GenericPayloadSerializer<Status> {

	public StatusSerializer() {
		super(Status.class);
	}

    protected String serializePayload(Status status) {
        XMLElement element = new XMLElement("status");
        element.addNode(new XMLTextNode(status.getText()));
        return element.serialize();
    }
}
