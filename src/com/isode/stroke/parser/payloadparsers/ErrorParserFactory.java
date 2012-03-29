/*
 * Copyright (c) 2012 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2011 Kevin Smith
 * All rights reserved.
 */ 
package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.PayloadParser;
import com.isode.stroke.parser.PayloadParserFactory;

/**
 * Parser factory for {@link ErrorPayload}
 *
 */
public class ErrorParserFactory implements PayloadParserFactory {    
    private FullPayloadParserFactoryCollection factories_;
    
    /**
     * Create the factory
     * @param factories reference to Payload parser factory collection, not null
     */
    public ErrorParserFactory(FullPayloadParserFactoryCollection factories) {
        this.factories_ = factories;
    }

    @Override
    public boolean canParse(final String element, final String ns, final AttributeMap map) {
        return element.equals("error");
    }
    
    @Override
    public PayloadParser createPayloadParser() {
        return new ErrorParser(factories_);
    }

    @Override
    public String toString() {
        return ErrorParserFactory.class.getSimpleName();
    }
}
