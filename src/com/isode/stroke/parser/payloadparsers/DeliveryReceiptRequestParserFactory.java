/*
 * Copyright (c) 2015, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2011 Tobias Markmann
 * Licensed under the BSD license.
 * See http://www.opensource.org/licenses/bsd-license.php for more information.
 */
package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.PayloadParser;
import com.isode.stroke.parser.PayloadParserFactory;

class DeliveryReceiptRequestParserFactory implements PayloadParserFactory {

    public DeliveryReceiptRequestParserFactory() {
    }

    @Override
    public boolean canParse(String element, String ns, AttributeMap attributes) {
        return "urn:xmpp:receipts".equals(ns) && "request".equals(element);
    }
    
    @Override
    public PayloadParser createPayloadParser() {
        return new DeliveryReceiptRequestParser();
    }

}
