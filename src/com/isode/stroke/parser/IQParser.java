/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.parser;

import com.isode.stroke.elements.IQ;

public class IQParser extends GenericStanzaParser<IQ> {

    public IQParser(PayloadParserFactoryCollection factories) {
        super(factories, new IQ());
    }

    @Override
    void handleStanzaAttributes(AttributeMap attributes) {
        String type = attributes.getAttribute("type");
        if ("set".equals(type)) {
            getStanzaGeneric().setType(IQ.Type.Set);
        } else if ("get".equals(type)) {
            getStanzaGeneric().setType(IQ.Type.Get);
        } else if ("result".equals(type)) {
            getStanzaGeneric().setType(IQ.Type.Result);
        } else if ("error".equals(type)) {
            getStanzaGeneric().setType(IQ.Type.Error);
        } else {
            getStanzaGeneric().setType(IQ.Type.Get);
        }
    }
}
