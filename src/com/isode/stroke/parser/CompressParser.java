/*
 * Copyright (c) 2010 Remko Tronçon
 * Licensed under the GNU General Public License v3.
 * See Documentation/Licenses/GPLv3.txt for more information.
 */
/*
 * Copyright (c) 2011, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.parser;

import com.isode.stroke.elements.CompressRequest;

class CompressParser extends GenericElementParser<CompressRequest> {

    private int currentDepth_ = 0;
    private String currentText_;
    private boolean inMethod_;

    public CompressParser() {
        super(CompressRequest.class);
    }

    @Override
    public void handleStartElement(String element, String ns, AttributeMap attributes) {
        if (currentDepth_ == 1 && element.equals("method")) {
            inMethod_ = true;
            currentText_ = "";
        }
        ++currentDepth_;
    }

    @Override
    public void handleEndElement(String el, String ns) {
        --currentDepth_;
        if (currentDepth_ == 1 && inMethod_) {
            getElementGeneric().setMethod(currentText_);
            inMethod_ = false;
        }
    }

    @Override
    public void handleCharacterData(String data) {
        currentText_ += data;
    }
}
