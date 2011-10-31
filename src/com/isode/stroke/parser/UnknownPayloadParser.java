/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.parser;

import com.isode.stroke.elements.Payload;

public class UnknownPayloadParser implements PayloadParser {

    public void handleStartElement(String element, String ns, AttributeMap attributes) {
        
    }

    public void handleEndElement(String element, String ns) {
        
    }

    public void handleCharacterData(String data) {
        
    }

    public Payload getPayload() {
        return null;
    }

}
