/*
 * Copyright (c) 2014, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2014, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.parser;

import com.isode.stroke.elements.PubSubOwnerPayload;
import com.isode.stroke.elements.PubSubOwnerPubSub;
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
            if (element == "configure"
                    && ns == "http://jabber.org/protocol/pubsub#owner") {
                currentPayloadParser_ = new PubSubOwnerConfigureParser(parsers_);
            }
            if (element == "subscriptions"
                    && ns == "http://jabber.org/protocol/pubsub#owner") {
                currentPayloadParser_ = new PubSubOwnerSubscriptionsParser(
                        parsers_);
            }
            if (element == "default"
                    && ns == "http://jabber.org/protocol/pubsub#owner") {
                currentPayloadParser_ = new PubSubOwnerDefaultParser(parsers_);
            }
            if (element == "purge"
                    && ns == "http://jabber.org/protocol/pubsub#owner") {
                currentPayloadParser_ = new PubSubOwnerPurgeParser(parsers_);
            }
            if (element == "affiliations"
                    && ns == "http://jabber.org/protocol/pubsub#owner") {
                currentPayloadParser_ = new PubSubOwnerAffiliationsParser(
                        parsers_);
            }
            if (element == "delete"
                    && ns == "http://jabber.org/protocol/pubsub#owner") {
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
