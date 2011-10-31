/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.serializer;

import com.isode.stroke.elements.IQ;
import com.isode.stroke.serializer.xml.XMLElement;

public class IQSerializer extends GenericStanzaSerializer<IQ> {

    public IQSerializer(PayloadSerializerCollection payloadSerializers) {
        super(IQ.class, "iq", payloadSerializers);
    }

    @Override
    void setStanzaSpecificAttributesGeneric(IQ iq, XMLElement element) {
        switch (iq.getType()) {
            case Get:
                element.setAttribute("type", "get");
                break;
            case Set:
                element.setAttribute("type", "set");
                break;
            case Result:
                element.setAttribute("type", "result");
                break;
            case Error:
                element.setAttribute("type", "error");
                break;
        }
    }
}
