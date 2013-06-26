/*
 * Copyright (c) 2011-2013, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2011, Kevin Smith.
 * All rights reserved.
 */

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.Last;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;

public class LastParser extends GenericPayloadParser<Last> {

    private int level_ = 0;

    public LastParser() {
        super(new Last());
    }

    public void handleStartElement(String element, String ns, AttributeMap attributes) {
        if (level_ == 0) {
            Long seconds = null;

		try {
			seconds = Long.parseLong(attributes.getAttribute("seconds"));
		}
		catch (NumberFormatException ex) {
		}
		getPayloadInternal().setSeconds(seconds);
	}
        ++level_;
    }

    public void handleEndElement(String element, String ns) {

    }

    public void handleCharacterData(String data) {

    }

}
