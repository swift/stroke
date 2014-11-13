/*
* Copyright (c) 2013-2015, Isode Limited, London, England.
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
            if ("items".equals(element) && "http://jabber.org/protocol/pubsub".equals(ns)) {
                currentPayloadParser_ = new PubSubItemsParser(parsers_);
            }
            if ("create".equals(element) && "http://jabber.org/protocol/pubsub".equals(ns)) {
                currentPayloadParser_ = new PubSubCreateParser(parsers_);
            }
            if ("publish".equals(element) && "http://jabber.org/protocol/pubsub".equals(ns)) {
                currentPayloadParser_ = new PubSubPublishParser(parsers_);
            }
            if ("affiliations".equals(element) && "http://jabber.org/protocol/pubsub".equals(ns)) {
                currentPayloadParser_ = new PubSubAffiliationsParser(parsers_);
            }
            if ("retract".equals(element) && "http://jabber.org/protocol/pubsub".equals(ns)) {
                currentPayloadParser_ = new PubSubRetractParser(parsers_);
            }
            if ("options".equals(element) && "http://jabber.org/protocol/pubsub".equals(ns)) {
                currentPayloadParser_ = new PubSubOptionsParser(parsers_);
            }
            if ("configure".equals(element) && "http://jabber.org/protocol/pubsub".equals(ns)) {
                currentPayloadParser_ = new PubSubConfigureParser(parsers_);
            }
            if ("default".equals(element) && "http://jabber.org/protocol/pubsub".equals(ns)) {
                currentPayloadParser_ = new PubSubDefaultParser(parsers_);
            }
            if ("subscriptions".equals(element) && "http://jabber.org/protocol/pubsub".equals(ns)) {
                currentPayloadParser_ = new PubSubSubscriptionsParser(parsers_);
            }
            if ("subscribe".equals(element) && "http://jabber.org/protocol/pubsub".equals(ns)) {
                currentPayloadParser_ = new PubSubSubscribeParser(parsers_);
            }
            if ("unsubscribe".equals(element) && "http://jabber.org/protocol/pubsub".equals(ns)) {
                currentPayloadParser_ = new PubSubUnsubscribeParser(parsers_);
            }
            if ("subscription".equals(element) && "http://jabber.org/protocol/pubsub".equals(ns)) {
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
                    if ("options".equals(element) && "http://jabber.org/protocol/pubsub".equals(ns)) {
                        optionsPayload_ = (PubSubOptions)currentPayloadParser_.getPayload();
                    }
                    else if ("configure".equals(element) && "http://jabber.org/protocol/pubsub".equals(ns)) {
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
