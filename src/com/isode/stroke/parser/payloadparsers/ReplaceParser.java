/*
* Copyright (c) 2014-2015, Isode Limited, London, England.
* All rights reserved.
*/
/*
 * Copyright (c) 2011 Vlad Voicu
 * Licensed under the Simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.Replace;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;

public class ReplaceParser extends GenericPayloadParser<Replace> {
    
    public ReplaceParser() {
        super(new Replace());
    }

    public void handleStartElement(String element, String ns, AttributeMap attributes) {
        if (level_ == 0) {
        	String id = attributes.getAttribute("id");
			getPayloadInternal().setID(id);
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
