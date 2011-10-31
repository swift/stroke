/*
 * Copyright (c) 2011, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.serializer;

import com.isode.stroke.elements.CompressFailure;
import com.isode.stroke.elements.Element;
import com.isode.stroke.serializer.xml.XMLElement;

class CompressFailureSerializer extends GenericElementSerializer<CompressFailure> {

    public CompressFailureSerializer() {
        super(CompressFailure.class);
    }

    public String serialize(Element element) {
        return new XMLElement("failure", "http://jabber.org/protocol/compress").serialize();
    }

}
