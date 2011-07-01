/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.parser;

import com.isode.stroke.elements.AuthRequest;
import com.isode.stroke.stringcodecs.Base64;

class AuthRequestParser extends GenericElementParser<AuthRequest> {

    public AuthRequestParser() {
        super(AuthRequest.class);
    }

    @Override
    public void handleStartElement(String a, String b, AttributeMap attribute) {
        if (depth_ == 0) {
            getElementGeneric().setMechanism(attribute.getAttribute("mechanism"));
        }
        ++depth_;
    }

    @Override
    public void handleEndElement(String a, String b) {
        --depth_;
        if (depth_ == 0) {
            getElementGeneric().setMessage(Base64.decode(text_));
        }
    }

    @Override
    public void handleCharacterData(String a) {
        text_ += a;
    }
    String text_ = "";
    int depth_ = 0;
}
