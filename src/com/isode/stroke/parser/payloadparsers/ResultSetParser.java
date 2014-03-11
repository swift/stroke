/*
* Copyright (c) 2014 Kevin Smith and Remko Tronçon
* All rights reserved.
*/

/*
* Copyright (c) 2014, Isode Limited, London, England.
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
            if (element == "first" && ns == "http://jabber.org/protocol/rsm") {
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
            if (element == "max") {
                getPayloadInternal().setMaxItems(Long.parseLong(currentText_));
            } else if (element == "count") {
                getPayloadInternal().setCount(Long.parseLong(currentText_));
            } else if (element == "first") {
                getPayloadInternal().setFirstID(currentText_);
            } else if (element == "last") {
                getPayloadInternal().setLastID(currentText_);
            } else if (element == "after") {
                getPayloadInternal().setAfter(currentText_);
            } else if (element == "before") {
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
