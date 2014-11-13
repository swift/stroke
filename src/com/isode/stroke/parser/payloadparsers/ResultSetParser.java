/*
* Copyright (c) 2014-2015, Isode Limited, London, England.
* All rights reserved.
*/

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.ResultSet;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;

public class ResultSetParser extends GenericPayloadParser<ResultSet> {

    public ResultSetParser() {
        super(new ResultSet());
    }

    public void handleStartElement(String element, String ns, AttributeMap attributes) {
        currentText_ = "";
        if (level_ == 1) {
            if ("first".equals(element) && "http://jabber.org/protocol/rsm".equals(ns)) {
                String attributeValue = attributes.getAttributeValue("index");
                if (attributeValue != null) {
                    getPayloadInternal().setFirstIDIndex(Long.parseLong(attributeValue));
                }
            }
        }
        ++level_;
    }
    
    public void handleEndElement(String element, String ns) {
        --level_;
        if (level_ == 1) {
            if ("max".equals(element)) {
                getPayloadInternal().setMaxItems(Long.parseLong(currentText_));
            } else if ("count".equals(element)) {
                getPayloadInternal().setCount(Long.parseLong(currentText_));
            } else if ("first".equals(element)) {
                getPayloadInternal().setFirstID(currentText_);
            } else if ("last".equals(element)) {
                getPayloadInternal().setLastID(currentText_);
            } else if ("after".equals(element)) {
                getPayloadInternal().setAfter(currentText_);
            } else if ("before".equals(element)) {
                getPayloadInternal().setBefore(currentText_);
            }
        }
    }
    
    public void handleCharacterData(String data) {
        currentText_ += data;
    }

    private String currentText_;
    private int level_;
}
