/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/
/*
* Copyright (c) 2014, Remko Tronçon.
* All rights reserved.
*/

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.PubSub;
import com.isode.stroke.elements.PubSubConfigure;
import com.isode.stroke.elements.PubSubCreate;
import com.isode.stroke.elements.PubSubOptions;
import com.isode.stroke.elements.PubSubPayload;
import com.isode.stroke.elements.PubSubSubscribe;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;
import com.isode.stroke.parser.PayloadParser;
import com.isode.stroke.parser.PayloadParserFactoryCollection;

public class PubSubParser extends GenericPayloadParser<PubSub> {
    
    public PubSubParser(PayloadParserFactoryCollection parsers) {
        super(new PubSub());
        parsers_ = parsers;
        level_ = 0;
    }
    
    public void handleStartElement(String element, String ns, AttributeMap attributes) {
        if (level_ == 1) {
            if (element == "items" && ns == "http://jabber.org/protocol/pubsub") {
                currentPayloadParser_ = new PubSubItemsParser(parsers_);
            }
            if (element == "create" && ns == "http://jabber.org/protocol/pubsub") {
                currentPayloadParser_ = new PubSubCreateParser(parsers_);
            }
            if (element == "publish" && ns == "http://jabber.org/protocol/pubsub") {
                currentPayloadParser_ = new PubSubPublishParser(parsers_);
            }
            if (element == "affiliations" && ns == "http://jabber.org/protocol/pubsub") {
                currentPayloadParser_ = new PubSubAffiliationsParser(parsers_);
            }
            if (element == "retract" && ns == "http://jabber.org/protocol/pubsub") {
                currentPayloadParser_ = new PubSubRetractParser(parsers_);
            }
            if (element == "options" && ns == "http://jabber.org/protocol/pubsub") {
                currentPayloadParser_ = new PubSubOptionsParser(parsers_);
            }
            if (element == "configure" && ns == "http://jabber.org/protocol/pubsub") {
                currentPayloadParser_ = new PubSubConfigureParser(parsers_);
            }
            if (element == "default" && ns == "http://jabber.org/protocol/pubsub") {
                currentPayloadParser_ = new PubSubDefaultParser(parsers_);
            }
            if (element == "subscriptions" && ns == "http://jabber.org/protocol/pubsub") {
                currentPayloadParser_ = new PubSubSubscriptionsParser(parsers_);
            }
            if (element == "subscribe" && ns == "http://jabber.org/protocol/pubsub") {
                currentPayloadParser_ = new PubSubSubscribeParser(parsers_);
            }
            if (element == "unsubscribe" && ns == "http://jabber.org/protocol/pubsub") {
                currentPayloadParser_ = new PubSubUnsubscribeParser(parsers_);
            }
            if (element == "subscription" && ns == "http://jabber.org/protocol/pubsub") {
                currentPayloadParser_ = new PubSubSubscriptionParser(parsers_);
            }
        }
        
        if (level_>=1 && currentPayloadParser_!=null) {
            currentPayloadParser_.handleStartElement(element, ns, attributes);
        }
        ++level_;
    }
    
    public void handleEndElement(String element, String ns) {
        --level_;
        if (currentPayloadParser_!=null) {
            if (level_ >= 1) {
                currentPayloadParser_.handleEndElement(element, ns);
            }
            
            if (level_ == 1) {
                if (currentPayloadParser_ != null) {
                    if (element == "options" && ns == "http://jabber.org/protocol/pubsub") {
                        optionsPayload_ = (PubSubOptions)currentPayloadParser_.getPayload();
                    }
                    else if (element == "configure" && ns == "http://jabber.org/protocol/pubsub") {
                        configurePayload_ = (PubSubConfigure)currentPayloadParser_.getPayload();
                    }
                    else {
                        getPayloadInternal().setPayload((PubSubPayload)currentPayloadParser_.getPayload());
                    }
                }
                currentPayloadParser_ = null;
            }
            
            if (level_ == 0) {
                PubSubCreate create = (PubSubCreate)getPayloadInternal().getPayload();
                if (create != null) {
                    if (configurePayload_ != null) {
                        create.setConfigure(configurePayload_);
                    }
                }
                PubSubSubscribe subscribe = (PubSubSubscribe)getPayloadInternal().getPayload();
                if (subscribe != null) {
                    if (optionsPayload_ != null) {
                        subscribe.setOptions(optionsPayload_);
                    }
                }
            }
        }
    }
    
    public void handleCharacterData(String data) {
        if (level_>1 && currentPayloadParser_!=null) {
            currentPayloadParser_.handleCharacterData(data);
        }
    }
    
    
    PayloadParserFactoryCollection parsers_;
    int level_;
    PayloadParser currentPayloadParser_;
    PubSubConfigure configurePayload_;
    PubSubOptions optionsPayload_;
    
}
