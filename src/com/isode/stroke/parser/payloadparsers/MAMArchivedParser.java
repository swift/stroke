/*
* Copyright (c) 2014 Kevin Smith and Remko Tronçon
* All rights reserved.
*/

/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.MAMArchived;
import com.isode.stroke.jid.JID;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;

public class MAMArchivedParser extends GenericPayloadParser<MAMArchived> {
    public MAMArchivedParser() {
        super(new MAMArchived());
    }

    public void handleStartElement(String element, String ns, AttributeMap attributes) {
        if (level_ == 0) {
            String attributeValue = attributes.getAttributeValue("by");
            if (attributeValue != null) {
                getPayloadInternal().setBy(JID.fromString(attributeValue));
            }
            attributeValue = attributes.getAttributeValue("id");
            if (attributeValue != null) {
                getPayloadInternal().setID(attributeValue);
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
