/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.Body;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLTextNode;

/**
 * Body to String.
 */
public class BodySerializer extends GenericPayloadSerializer<Body> {

    public BodySerializer() {
        super(Body.class);
    }

    @Override
    protected String serializePayload(Body body) {
        XMLTextNode textNode = new XMLTextNode(body.getText());
        return "<body>" + textNode.serialize() + "</body>";
    }
}
