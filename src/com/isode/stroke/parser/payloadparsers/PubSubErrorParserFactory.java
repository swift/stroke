/*
 * Copyright (c) 2014, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2014, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.PayloadParser;
import com.isode.stroke.parser.PayloadParserFactory;

public class PubSubErrorParserFactory implements PayloadParserFactory {
    
    @Override
    public boolean canParse(String element, String ns, AttributeMap attributes) {
        return ns.equals("http://jabber.org/protocol/pubsub#errors");
    }
    
    @Override
    public PayloadParser createPayloadParser() {
        return new PubSubErrorParser();
    }
    
}
