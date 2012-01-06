/*
 * Copyright (c) 2012 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon
 * All rights reserved.
 */

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.Form;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.PayloadParser;
import com.isode.stroke.parser.PayloadParserFactory;

/**
 * Parser factory for {@link Form} element.
 */
public class FormParserFactory implements PayloadParserFactory {
    /**
     * Constructor
     */
    public FormParserFactory() {
    }

    public boolean canParse(String element, String ns, AttributeMap attributes) {
        return ns.equals("jabber:x:data");
    }

    public PayloadParser createPayloadParser() {
        return new FormParser();
    }

    @Override
    public String toString() {
        return FormParserFactory.class.getSimpleName();
    }
}
