/*
 * Copyright (c) 2013-2015, Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.PubSubOwnerPayload;
import com.isode.stroke.elements.PubSubOwnerPubSub;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;
import com.isode.stroke.parser.PayloadParser;
import com.isode.stroke.parser.PayloadParserFactoryCollection;
import com.isode.stroke.parser.payloadparsers.PubSubOwnerAffiliationsParser;
import com.isode.stroke.parser.payloadparsers.PubSubOwnerConfigureParser;
import com.isode.stroke.parser.payloadparsers.PubSubOwnerDefaultParser;
import com.isode.stroke.parser.payloadparsers.PubSubOwnerDeleteParser;
import com.isode.stroke.parser.payloadparsers.PubSubOwnerPurgeParser;
import com.isode.stroke.parser.payloadparsers.PubSubOwnerSubscriptionsParser;

public class PubSubOwnerPubSubParser extends
        GenericPayloadParser<PubSubOwnerPubSub> {
    
    public PubSubOwnerPubSubParser(PayloadParserFactoryCollection parsers) {
        super(new PubSubOwnerPubSub());
        parsers_ = parsers;
        level_ = 0;
    }
    
    public void handleStartElement(String element, String ns,
            AttributeMap attributes) {
        if (level_ == 1) {
            if ("configure".equals(element)
                    && "http://jabber.org/protocol/pubsub#owner".equals(ns)) {
                currentPayloadParser_ = new PubSubOwnerConfigureParser(parsers_);
            }
            if ("subscriptions".equals(element)
                    && "http://jabber.org/protocol/pubsub#owner".equals(ns)) {
                currentPayloadParser_ = new PubSubOwnerSubscriptionsParser(
                        parsers_);
            }
            if ("default".equals(element)
                    && "http://jabber.org/protocol/pubsub#owner".equals(ns)) {
                currentPayloadParser_ = new PubSubOwnerDefaultParser(parsers_);
            }
            if ("purge".equals(element)
                    && "http://jabber.org/protocol/pubsub#owner".equals(ns)) {
                currentPayloadParser_ = new PubSubOwnerPurgeParser(parsers_);
            }
            if ("affiliations".equals(element)
                    && "http://jabber.org/protocol/pubsub#owner".equals(ns)) {
                currentPayloadParser_ = new PubSubOwnerAffiliationsParser(
                        parsers_);
            }
            if ("delete".equals(element)
                    && "http://jabber.org/protocol/pubsub#owner".equals(ns)) {
                currentPayloadParser_ = new PubSubOwnerDeleteParser(parsers_);
            }
        }
        
        if (level_ >= 1 && currentPayloadParser_ != null) {
            currentPayloadParser_.handleStartElement(element, ns, attributes);
        }
        ++level_;
    }
    
    public void handleEndElement(String element, String ns) {
        --level_;
        if (currentPayloadParser_ != null) {
            if (level_ >= 1) {
                currentPayloadParser_.handleEndElement(element, ns);
            }
            
            if (level_ == 1) {
                if (currentPayloadParser_ != null) {
                    getPayloadInternal().setPayload(
                            (PubSubOwnerPayload) currentPayloadParser_
                                    .getPayload());
                }
                currentPayloadParser_ = null;
            }
        }
    }
    
    public void handleCharacterData(String data) {
        if (level_ > 1 && currentPayloadParser_ != null) {
            currentPayloadParser_.handleCharacterData(data);
        }
    }
    
    PayloadParserFactoryCollection parsers_;
    int level_;
    PayloadParser currentPayloadParser_;
    
}
