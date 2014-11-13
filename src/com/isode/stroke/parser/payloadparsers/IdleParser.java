/*
 * Copyright (c) 2011-2015, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2013 Tobias Markmann
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.parser.payloadparsers;

import java.util.Date;

import com.isode.stroke.base.DateTime;
import com.isode.stroke.elements.Idle;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;

public class IdleParser extends GenericPayloadParser<Idle> {

    private int level_ = 0;

    public IdleParser() {
        super(new Idle());
    }

    public void handleStartElement(String element, String ns, AttributeMap attributes) {
        if (level_ == 0) {
            Date since = DateTime.stringToDate(attributes.getAttribute("since"));
    		getPayloadInternal().setSince(since);
    	}
        ++level_;
    }

    public void handleEndElement(String element, String ns) {
        --level_;
    }

    public void handleCharacterData(String data) {

    }

}
