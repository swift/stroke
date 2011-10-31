/*
 * Copyright (c) 2011, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2011, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.serializer;

import com.isode.stroke.elements.Element;
import com.isode.stroke.elements.StreamResumed;
import com.isode.stroke.serializer.xml.XMLElement;

class StreamResumedSerializer extends GenericElementSerializer<StreamResumed> {

    public StreamResumedSerializer() {
        super(StreamResumed.class);
    }

    public String serialize(Element el) {
        StreamResumed e = (StreamResumed)el;
	XMLElement element = new XMLElement("resumed", "urn:xmpp:sm:2");
	element.setAttribute("previd", e.getResumeID());
	if (e.getHandledStanzasCount() != null) {
		element.setAttribute("h", Long.toString(e.getHandledStanzasCount()));
	}
	return element.serialize();
    }

}
