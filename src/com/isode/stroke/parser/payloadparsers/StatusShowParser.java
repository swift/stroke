/*
* Copyright (c) 2010-2015, Isode Limited, London, England.
* All rights reserved.
*/

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.StatusShow;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.GenericPayloadParser;

public class StatusShowParser extends GenericPayloadParser<StatusShow> {
    
    public StatusShowParser() {
        super(new StatusShow());
    }

    public void handleStartElement(String element, String ns, AttributeMap attributes) {
        ++level_;
    }
    
    public void handleEndElement(String element, String ns) {
        --level_;
        if (level_ == 0) {
            if ("away".equals(text_)) {
                getPayloadInternal().setType(StatusShow.Type.Away);
            }
            else if ("chat".equals(text_)) {
                getPayloadInternal().setType(StatusShow.Type.FFC);
            }
            else if ("xa".equals(text_)) {
                getPayloadInternal().setType(StatusShow.Type.XA);
            }
            else if ("dnd".equals(text_)) {
                getPayloadInternal().setType(StatusShow.Type.DND);
            }
            else {
                getPayloadInternal().setType(StatusShow.Type.Online);
            }
        }
    }
    
    public void handleCharacterData(String data) {
        text_ = text_ == null ? data : text_ + data;
    }

    private int level_;
    private String text_;
}
