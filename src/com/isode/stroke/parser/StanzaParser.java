/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.parser;

import com.isode.stroke.elements.Element;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.elements.Stanza;
import com.isode.stroke.jid.JID;

public abstract class StanzaParser implements ElementParser {

    protected int currentDepth_ = 0;
    protected final PayloadParserFactoryCollection factories_;
    protected PayloadParser currentPayloadParser_;

    public StanzaParser(PayloadParserFactoryCollection factories) {
        factories_ = factories;
    }

    void handleStanzaAttributes(AttributeMap map) {
    }

    Stanza getStanza() {
        return (Stanza) getElement();
    }

    private boolean inPayload() {
        return currentDepth_ > 1;
    }

    private boolean inStanza() {
        return currentDepth_ > 0;
    }

    public void handleStartElement(String element, String ns, AttributeMap attributes) {
        if (inStanza()) {
            if (!inPayload()) {
                assert currentPayloadParser_ == null;
                PayloadParserFactory payloadParserFactory = factories_.getPayloadParserFactory(element, ns, attributes);
                if (payloadParserFactory != null) {
                    currentPayloadParser_ = payloadParserFactory.createPayloadParser();
                } else {
                    currentPayloadParser_ = new UnknownPayloadParser();
                }
            }
            assert currentPayloadParser_ != null;
            currentPayloadParser_.handleStartElement(element, ns, attributes);
        } else {
            String from = attributes.getAttribute("from");
            if (from != null) {
                getStanza().setFrom(JID.fromString(from));
            }
            String to = attributes.getAttribute("to");
            if (to != null) {
                getStanza().setTo(JID.fromString(to));
            }
            String id = attributes.getAttribute("id");
            if (id != null) {
                getStanza().setID(id);
            }
            handleStanzaAttributes(attributes);
        }
        ++currentDepth_;
    }

    public void handleEndElement(String element, String ns) {
        assert (inStanza());
        if (inPayload()) {
            assert currentPayloadParser_ != null;
            currentPayloadParser_.handleEndElement(element, ns);
            --currentDepth_;
            if (!inPayload()) {
                Payload payload = currentPayloadParser_.getPayload();
                if (payload != null) {
                    getStanza().addPayload(payload);
                }
                currentPayloadParser_ = null;
            }
        } else {
            --currentDepth_;
        }
    }

    public void handleCharacterData(String data) {
        if (currentPayloadParser_ != null) {
            currentPayloadParser_.handleCharacterData(data);
        }
    }
}
