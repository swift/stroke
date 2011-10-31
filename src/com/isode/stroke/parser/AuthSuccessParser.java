/*
 * Copyright (c) 2010-2011, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.parser;

import com.isode.stroke.elements.AuthSuccess;
import com.isode.stroke.stringcodecs.Base64;

class AuthSuccessParser extends GenericElementParser<AuthSuccess> {

    public AuthSuccessParser() {
        super(AuthSuccess.class);
    }

    @Override
    public void handleStartElement(String a, String b, AttributeMap attribute) {
        ++depth_;
    }

    @Override
    public void handleEndElement(String a, String b) {
        --depth_;
        if (depth_ == 0) {
            getElementGeneric().setValue(Base64.decode(text_));
        }
    }

    @Override
    public void handleCharacterData(String a) {
        text_ += a;
    }
    String text_ = "";
    int depth_ = 0;
}
