/*
 * Copyright (c) 2010, 2011 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.StartSession;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;

public class StartSessionParser extends GenericPayloadParser<StartSession> {

    public StartSessionParser() {
        super(new StartSession());
    }
    
    public void handleStartElement(String element, String ns, AttributeMap attributes) {
        
    }

    public void handleEndElement(String element, String ns) {
        
    }

    public void handleCharacterData(String data) {
        
    }


}
