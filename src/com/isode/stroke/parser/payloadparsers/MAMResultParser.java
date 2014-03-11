/*
* Copyright (c) 2014 Kevin Smith and Remko Tronçon
* All rights reserved.
*/

/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.Forwarded;
import com.isode.stroke.elements.MAMResult;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;
import com.isode.stroke.parser.PayloadParserFactoryCollection;

public class MAMResultParser extends GenericPayloadParser<MAMResult> {
    public MAMResultParser(PayloadParserFactoryCollection factories) {
        super(new MAMResult());
        factories_ = factories;
    }

    public void handleStartElement(String element, String ns, AttributeMap attributes) {
        if (level_ == 0) {
            String attributeValue = attributes.getAttributeValue("id");
            if (attributeValue != null) {
                getPayloadInternal().setID(attributeValue);
            }
            attributeValue = attributes.getAttributeValue("queryid");
            if (attributeValue != null) {
                getPayloadInternal().setQueryID(attributeValue);
            }
        } else if (level_ == 1) {
            if (element == "forwarded" && ns == "urn:xmpp:forward:0") {
                payloadParser_ = new ForwardedParser(factories_);
            }
        }
    
        if (payloadParser_ != null) { /* parsing a nested payload? */
            payloadParser_.handleStartElement(element, ns, attributes);
        }
    
        ++level_;
    }
    
    public void handleEndElement(String element, String ns) {
        --level_;
        if (payloadParser_ != null && level_ >= 1) {
            payloadParser_.handleEndElement(element, ns);
        }
        if (payloadParser_ != null && level_ == 1) { /* done parsing nested stanza? */
            getPayloadInternal().setPayload((Forwarded)payloadParser_.getPayload());
            payloadParser_ = null;
        }
    }
    
    public void handleCharacterData(String data) {
        if (payloadParser_ != null) {
            payloadParser_.handleCharacterData(data);
        }
    }

    private ForwardedParser payloadParser_;
    private PayloadParserFactoryCollection factories_;
    private int level_;
}
