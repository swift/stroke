/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2011, Kevin Smith
 * All rights reserved.
 */
package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.MUCOwnerPayload;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;
import com.isode.stroke.parser.PayloadParser;
import com.isode.stroke.parser.PayloadParserFactory;
import com.isode.stroke.parser.PayloadParserFactoryCollection;


/**
 * Class representing a parser for MUC Owner payload
 *
 */
public class MUCOwnerPayloadParser extends GenericPayloadParser<MUCOwnerPayload> {
    private PayloadParserFactoryCollection factories_;
    private int level;
    private PayloadParser currentPayloadParser;

    /**
     * Create the parser
     * @param factories payload parser factory collection, not null
     */
    public MUCOwnerPayloadParser(PayloadParserFactoryCollection factories) {
        super(new MUCOwnerPayload());
        this.factories_ = factories;
        level = 0;
    }

    @Override
    public void handleStartElement(String element, String ns, AttributeMap attributes) {
        if (level == 1) {
            PayloadParserFactory payloadParserFactory = factories_.getPayloadParserFactory(
                    element, ns, attributes);      
            if (payloadParserFactory != null) {
                currentPayloadParser = payloadParserFactory.createPayloadParser();
            }       
        }               

        if (level >= 1 && currentPayloadParser != null) {
            currentPayloadParser.handleStartElement(element, ns, attributes);
        }               
        ++level;
    }       

    @Override
    public void handleEndElement(String element, String ns) {
        --level;
        if (currentPayloadParser != null) {
            if (level >= 1) {
                currentPayloadParser.handleEndElement(element, ns);
            }

            if (level == 1) {
                getPayloadInternal().setPayload(currentPayloadParser.getPayload());
            }
        }
    }

    @Override
    public void handleCharacterData(String data) {
        if (level > 1 && currentPayloadParser != null) {
            currentPayloadParser.handleCharacterData(data);
        }
    }
}
