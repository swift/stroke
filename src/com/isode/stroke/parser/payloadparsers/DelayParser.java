/*
* Copyright (c) 2014 Kevin Smith and Remko Tronçon
* All rights reserved.
*/

/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/

package com.isode.stroke.parser.payloadparsers;

import java.util.Date;
import com.isode.stroke.base.DateTime;
import com.isode.stroke.elements.Delay;
import com.isode.stroke.jid.JID;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;

public class DelayParser extends GenericPayloadParser<Delay> {
    
    public DelayParser() {
        super(new Delay());
    }

    public void handleStartElement(String element, String ns, AttributeMap attributes) {
        if (level_ == 0) {
            Date stamp = DateTime.stringToDate(attributes.getAttribute("stamp"));
            getPayloadInternal().setStamp(stamp);
            if (!attributes.getAttribute("from").isEmpty()) {
                String from = attributes.getAttribute("from");
                getPayloadInternal().setFrom(new JID(from));
            }
        }
        ++level_;
    }
    
    public void handleEndElement(String element, String ns) {
        --level_;
    }
    
    public void handleCharacterData(String data) {
    }

    private int level_;
}
