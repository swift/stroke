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

    public boolean canParse(String element, String ns,
            final AttributeMap attributes) {
        if (element == null) {
            throw new NullPointerException("'element' must not be null");
        }
        if (ns == null) {
            throw new NullPointerException("'ns' must not be null");
        }
        if (attributes == null) {
            throw new NullPointerException("'attributes' must not be null");
        }

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
