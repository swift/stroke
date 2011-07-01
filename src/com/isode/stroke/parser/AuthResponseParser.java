/*
 * Copyright (c) 2010 Remko Tronçon
 * Licensed under the GNU General Public License v3.
 * See Documentation/Licenses/GPLv3.txt for more information.
 */
/*
 * Copyright (c) 2010-2011, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.parser;

import com.isode.stroke.elements.AuthResponse;
import com.isode.stroke.stringcodecs.Base64;

class AuthResponseParser extends GenericElementParser<AuthResponse> {

    public AuthResponseParser() {
        super(AuthResponse.class);
    }

    @Override
    public void handleStartElement(String unused1, String unused2, AttributeMap unused3) {
        ++depth;
    }

    @Override
    public void handleEndElement(String unused1, String unused2) {
        --depth;
        if (depth == 0) {
            getElementGeneric().setValue(Base64.decode(text));
        }
    }

    @Override
    public void handleCharacterData(String text) {
        this.text += text;
    }
    private int depth = 0;
    private String text = "";
}
