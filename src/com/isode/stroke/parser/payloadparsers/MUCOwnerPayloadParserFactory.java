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
 * Factory for MUC Owner Payload Parser
 *
 */
public class MUCOwnerPayloadParserFactory implements PayloadParserFactory {
    
    private PayloadParserFactoryCollection factories;
    
    /**
     * Create the MUC Owner Payload Parser factory
     * @param factories reference to Payload Parser Factory Collection, not null
     */
    public MUCOwnerPayloadParserFactory(PayloadParserFactoryCollection factories)  {
        this.factories = factories;
    }

    @Override
    public boolean canParse(String element, String ns, AttributeMap map) {
        return "query".equals(element) && "http://jabber.org/protocol/muc#owner".equals(ns);
    }

    @Override
    public PayloadParser createPayloadParser() {
        return new MUCOwnerPayloadParser(factories);
    }
}
