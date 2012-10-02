/*
 * Copyright (c) 2010 Remko Tronçon
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.SoftwareVersion;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;

public class SoftwareVersionParser extends GenericPayloadParser<SoftwareVersion> {

    public SoftwareVersionParser() {
        super(new SoftwareVersion());
    }

    public void handleStartElement(String element, String ns, AttributeMap attributes) {
        level_++;
    }

    public void handleEndElement(String element, String ns) {
        --level_;
        if (level_ == PayloadLevel) {
            if (element.equals("name")) {
                getPayloadInternal().setName(currentText_);
            } else if (element.equals("version")) {
                getPayloadInternal().setVersion(currentText_);
            } else if (element.equals("os")) {
                getPayloadInternal().setOS(currentText_);
            }
            currentText_ = "";
        }

    }

    public void handleCharacterData(String data) {
        currentText_ += data;
    }

    private static final int TopLevel = 0;
    private static final int PayloadLevel = 1;
    private int level_ = TopLevel;
    private String currentText_ = "";


}
