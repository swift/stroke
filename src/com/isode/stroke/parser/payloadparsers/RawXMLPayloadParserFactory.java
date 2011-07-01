/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.PayloadParser;
import com.isode.stroke.parser.PayloadParserFactory;

public class RawXMLPayloadParserFactory implements PayloadParserFactory {

    public boolean canParse(String element, String ns, AttributeMap attributes) {
        return true;
    }

    public PayloadParser createPayloadParser() {
        return new RawXMLPayloadParser();
    }

}
