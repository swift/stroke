/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.Body;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;

public class BodyParser extends GenericPayloadParser<Body> {

    private int level_ = 0;
    private String text_ = "";

    public BodyParser() {
        super(new Body());
    }

    public void handleStartElement(String element, String ns, AttributeMap attributes) {
        ++level_;
    }

    public void handleEndElement(String element, String ns) {
        --level_;
	if (level_ == 0) {
		getPayloadInternal().setText(text_);
	}
    }

    public void handleCharacterData(String data) {
        text_ += data;
    }

}
