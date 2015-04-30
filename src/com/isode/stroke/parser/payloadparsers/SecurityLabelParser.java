/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.SecurityLabel;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;
import com.isode.stroke.parser.SerializingParser;

public class SecurityLabelParser extends GenericPayloadParser<SecurityLabel> {

    private int level_ = 0;
    private final static int TopLevel = 0;
    private final static int PayloadLevel = 1;
    private final static int DisplayMarkingOrLabelLevel = 2;
    private final static int SecurityLabelLevel = 3;
    
    private SerializingParser labelParser_;
    
    private String currentText_ = "";

    public SecurityLabelParser() {
        super(new SecurityLabel());
    }

    public void handleStartElement(String element, String ns, AttributeMap attributes) {
        ++level_;
        if (level_ == DisplayMarkingOrLabelLevel) {
            if ("displaymarking".equals(element)) {
                currentText_ = "";
                getPayloadInternal().setBackgroundColor(attributes.getAttribute("bgcolor"));
                getPayloadInternal().setForegroundColor(attributes.getAttribute("fgcolor"));
            }
            else if ("label".equals(element) || "equivalentlabel".equals(element)) {
                assert(labelParser_ == null);
                labelParser_ = new SerializingParser();
            }
        }
        else if (level_ >= SecurityLabelLevel && labelParser_ != null) {
            labelParser_.handleStartElement(element, ns, attributes);
        }
    }

    public void handleEndElement(String element, String ns) {
        if (level_ == DisplayMarkingOrLabelLevel) {
            if ("displaymarking".equals(element)) {
                getPayloadInternal().setDisplayMarking(currentText_);
            }
            else if (labelParser_ != null) {
                if ("label".equals(element)) {
                    getPayloadInternal().setLabel(labelParser_.getResult());
                }
                else {
                    getPayloadInternal().addEquivalentLabel(labelParser_.getResult());
                }
                labelParser_ = null;
            }
        }
        else if (labelParser_ != null && level_ >= SecurityLabelLevel) {
            labelParser_.handleEndElement(element, ns);
        }
        --level_;
    }

    public void handleCharacterData(String data) {
        if (labelParser_ != null) {
            labelParser_.handleCharacterData(data);
        }
        else {
            currentText_ += data;
        }
    }

    public SecurityLabel getLabelPayload() {
        return getPayloadInternal();
    }
}
