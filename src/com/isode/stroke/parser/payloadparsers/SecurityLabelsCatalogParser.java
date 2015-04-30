/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.SecurityLabel;
import com.isode.stroke.elements.SecurityLabelsCatalog;
import com.isode.stroke.jid.JID;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;

public class SecurityLabelsCatalogParser extends GenericPayloadParser<SecurityLabelsCatalog> {

    private int level_ = 0;
    private final static int TopLevel     = 0;
    private final static int PayloadLevel = 1;
    private final static int ItemLevel    = 2;
    private final static int LabelLevel   = 3;
    
    private final SecurityLabelParserFactory labelParserFactory_;
    private SecurityLabelParser labelParser_;
    private SecurityLabelsCatalog.Item currentItem_;

    public SecurityLabelsCatalogParser() {
        super(new SecurityLabelsCatalog());
        labelParserFactory_ = new SecurityLabelParserFactory();
    }

    public void handleStartElement(String element, String ns, AttributeMap attributes) {
        ++level_;
        if (level_ == PayloadLevel) {
            getPayloadInternal().setTo(new JID(attributes.getAttribute("to")));
            getPayloadInternal().setName(attributes.getAttribute("name"));
            getPayloadInternal().setDescription(attributes.getAttribute("desc"));
        }
        else if (level_ == ItemLevel && "item".equals(element) && "urn:xmpp:sec-label:catalog:2".equals(ns)) {
            currentItem_ = new SecurityLabelsCatalog.Item();
            currentItem_.setSelector(attributes.getAttribute("selector"));
            currentItem_.setIsDefault(attributes.getBoolAttribute("default", false));
        }
        else if (level_ == LabelLevel) {
            assert(labelParser_ == null);
            if (labelParserFactory_.canParse(element, ns, attributes)) {
                labelParser_ = (SecurityLabelParser)(labelParserFactory_.createPayloadParser());
                assert (labelParser_) != null;
            }
        }

        if (labelParser_ != null) {
            labelParser_.handleStartElement(element, ns, attributes);
        }
    }

    public void handleEndElement(String element, String ns) {
        if (labelParser_ != null) {
            labelParser_.handleEndElement(element, ns);
        }
        if (level_ == LabelLevel && labelParser_ != null && currentItem_ != null) {
            SecurityLabel currentLabel = labelParser_.getLabelPayload();
            assert (currentLabel) != null;
            currentItem_.setLabel(currentLabel);
            labelParser_ = null;
        }
        else if (level_ == ItemLevel && "item".equals(element) && "urn:xmpp:sec-label:catalog:2".equals(ns)) {
            if (currentItem_ != null) {
                getPayloadInternal().addItem(currentItem_);
                currentItem_ = null;
            }
        }
        --level_;
    }

    public void handleCharacterData(String data) {
        if (labelParser_ != null) {
            labelParser_.handleCharacterData(data);
        }
    }

}
