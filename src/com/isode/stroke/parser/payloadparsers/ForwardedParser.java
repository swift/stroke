/*
 * Copyright (c) 2014 Kevin Smith and Remko Tronçon
 * All rights reserved.
 */

/*
 * Copyright (c) 2014, Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.Delay;
import com.isode.stroke.elements.Forwarded;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;
import com.isode.stroke.parser.IQParser;
import com.isode.stroke.parser.MessageParser;
import com.isode.stroke.parser.PayloadParserFactoryCollection;
import com.isode.stroke.parser.PresenceParser;
import com.isode.stroke.parser.StanzaParser;

public class ForwardedParser extends GenericPayloadParser<Forwarded> {
    public ForwardedParser(PayloadParserFactoryCollection factories) {
        super(new Forwarded());
        factories_ = factories;
    }
    
    public void handleStartElement(String element, String ns, AttributeMap attributes) {
        if (level_ == 1) {
            if (element == "iq") { /* begin parsing a nested stanza? */
                childParser_ = new IQParser(factories_);
            } else if (element == "message") {
                childParser_ = new MessageParser(factories_);
            } else if (element == "presence") {
                childParser_ = new PresenceParser(factories_);
            } else if (element == "delay" && ns == "urn:xmpp:delay") {
                delayParser_ = new DelayParser();
            }
        }
        if (childParser_ != null) { /* parsing a nested stanza? */
            childParser_.handleStartElement(element, ns, attributes);
        }
        if (delayParser_ != null) { /* parsing a nested delay payload? */
            delayParser_.handleStartElement(element, ns, attributes);
        }
        ++level_;
    }
    
    public void handleEndElement(String element, String ns) {
        --level_;
        if (childParser_ != null && level_ >= 1) {
            childParser_.handleEndElement(element, ns);
        }
        if (childParser_ != null && level_ == 1) { /* done parsing nested stanza? */
            getPayloadInternal().setStanza(childParser_.getStanza());
            childParser_ = null;
        }
        if (delayParser_ != null && level_ >= 1) {
            delayParser_.handleEndElement(element, ns);
        }
        if (delayParser_ != null && level_ == 1) { /* done parsing nested delay payload? */
            getPayloadInternal().setDelay((Delay) delayParser_.getPayload());
            delayParser_ = null;
        }
    }
    
    public void handleCharacterData(String data) {
        if (childParser_ != null) {
            childParser_.handleCharacterData(data);
        }
        if (delayParser_ != null) {
            delayParser_.handleCharacterData(data);
        }
    }
    
    private PayloadParserFactoryCollection factories_;
    private StanzaParser childParser_;
    private DelayParser delayParser_;
    private int level_;
}
