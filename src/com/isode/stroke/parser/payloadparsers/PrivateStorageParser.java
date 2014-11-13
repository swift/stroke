/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.PrivateStorage;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;
import com.isode.stroke.parser.PayloadParser;
import com.isode.stroke.parser.PayloadParserFactory;
import com.isode.stroke.parser.PayloadParserFactoryCollection;

public class PrivateStorageParser extends GenericPayloadParser<PrivateStorage> {

    private PayloadParserFactoryCollection factories;
    private int level;
    private PayloadParser currentPayloadParser;
    
    public PrivateStorageParser(PayloadParserFactoryCollection factories) {
        super(new PrivateStorage());
        this.factories = factories;
    }

    public void handleStartElement(String element, String ns, AttributeMap attributes) {
        if (level == 1) {
            PayloadParserFactory payloadParserFactory = factories.getPayloadParserFactory(element, ns, attributes);
            if (payloadParserFactory != null) {
                currentPayloadParser = payloadParserFactory.createPayloadParser();
            }
        }

        if (level >= 1 && currentPayloadParser != null) {
            currentPayloadParser.handleStartElement(element, ns, attributes);
        }
        ++level;
    }

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

    public void handleCharacterData(String data) {
        if (level > 1 && currentPayloadParser != null) {
            currentPayloadParser.handleCharacterData(data);
        }
    }
}
