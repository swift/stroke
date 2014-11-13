/*
 * Copyright (c) 2010-2015 Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.PayloadParser;
import com.isode.stroke.parser.PayloadParserFactory;
import com.isode.stroke.parser.PayloadParserFactoryCollection;

public class PrivateStorageParserFactory implements PayloadParserFactory {

    private PayloadParserFactoryCollection factories;

    public PrivateStorageParserFactory(PayloadParserFactoryCollection factories) {
        this.factories = factories;
    }

    @Override
    public boolean canParse(String element, String ns,  AttributeMap map) {
        return "query".equals(element) && "jabber:iq:private".equals(ns);
    }

    @Override
    public PayloadParser createPayloadParser() {
        return new PrivateStorageParser(factories);
    }
}
