/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.Payload;
import com.isode.stroke.elements.RosterItemPayload;
import com.isode.stroke.elements.RosterPayload;
import com.isode.stroke.jid.JID;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;

public class RosterParser extends GenericPayloadParser<RosterPayload> {

    public RosterParser() {
        super(new RosterPayload());
    }

    public void handleStartElement(String element, String ns, AttributeMap attributes) {
        if (level_ == PayloadLevel) {
            if (element.equals("item")) {
                inItem_ = true;
                currentItem_ = new RosterItemPayload();

                currentItem_.setJID(JID.fromString(attributes.getAttribute("jid")));
                currentItem_.setName(attributes.getAttribute("name"));

                String subscription = attributes.getAttribute("subscription");
                if ("both".equals(subscription)) {
                    currentItem_.setSubscription(RosterItemPayload.Subscription.Both);
                } else if ("to".equals(subscription)) {
                    currentItem_.setSubscription(RosterItemPayload.Subscription.To);
                } else if ("frome".equals(subscription)) {
                    currentItem_.setSubscription(RosterItemPayload.Subscription.From);
                } else if ("remove".equals(subscription)) {
                    currentItem_.setSubscription(RosterItemPayload.Subscription.Remove);
                } else {
                    currentItem_.setSubscription(RosterItemPayload.Subscription.None);
                }

                if ("subscribe".equals(attributes.getAttribute("ask"))) {
                    currentItem_.setSubscriptionRequested();
                }
            }
        } else if (level_ == ItemLevel) {
            if (element.equals("group")) {
                currentText_ = "";
            }
        }
        ++level_;
    }

    public void handleEndElement(String element, String ns) {
        --level_;
        if (level_ == PayloadLevel) {
            if (inItem_) {
                getPayloadInternal().addItem(currentItem_);
                inItem_ = false;
            }
        } else if (level_ == ItemLevel) {
            if (element.equals("group")) {
                currentItem_.addGroup(currentText_);
            }
        }
    }

    public void handleCharacterData(String data) {
        currentText_ += data;
    }
    private final int TopLevel = 0;
    private final int PayloadLevel = 1;
    private final int ItemLevel = 2;
    private int level_ = TopLevel;
    private boolean inItem_ = false;
    private RosterItemPayload currentItem_;
    private String currentText_;
}
