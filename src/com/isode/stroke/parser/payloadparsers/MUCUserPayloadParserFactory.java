/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2011, Kevin Smith
 * All rights reserved.
 */
package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.PayloadParser;
import com.isode.stroke.parser.PayloadParserFactory;
import com.isode.stroke.parser.PayloadParserFactoryCollection;

/**
 * Factory for MUC User Payload Parser
 *
 */
public class MUCUserPayloadParserFactory implements PayloadParserFactory {
    
    private PayloadParserFactoryCollection factories;
    
    /**
     * Create the MUC User Payload Parser factory
     * @param factories reference to Payload Parser Factory Collection, not null
     */
    public MUCUserPayloadParserFactory(PayloadParserFactoryCollection factories) {
        this.factories = factories;
    }

    @Override
    public boolean canParse(String element, String ns,  AttributeMap map) {
        return "x".equals(element) && "http://jabber.org/protocol/muc#user".equals(ns);
    }

    @Override
    public PayloadParser createPayloadParser() {
        return new MUCUserPayloadParser(factories);
    }    
}
