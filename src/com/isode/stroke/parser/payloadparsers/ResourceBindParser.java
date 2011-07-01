/*
 * Copyright (c) 2010, 2011 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.ResourceBind;
import com.isode.stroke.jid.JID;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;

public class ResourceBindParser extends GenericPayloadParser<ResourceBind> {

    public ResourceBindParser() {
        super(new ResourceBind());
        level_ = 0;
        inJID_ = false;
        inResource_ = false;
    }

    public void handleStartElement(String element, String ns, AttributeMap attributes) {
        if (level_ == 1) {
            text_ = "";
            if (element.equals("resource")) {
                inResource_ = true;
            }
            if (element.equals("jid")) {
                inJID_ = true;
            }
        }
        ++level_;
    }

    public void handleEndElement(String element, String ns) {
        --level_;
        if (level_ == 1) {
            if (inJID_) {
                getPayloadInternal().setJID(JID.fromString(text_));
            } else if (inResource_) {
                getPayloadInternal().setResource(text_);
            }
        }
    }

    public void handleCharacterData(String data) {
        text_ += data;
    }
    private int level_;
    private boolean inJID_;
    private boolean inResource_;
    private String text_ = "";
}
