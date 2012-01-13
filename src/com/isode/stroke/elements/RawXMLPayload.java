/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.elements;

/**
 * Unparsed content.
 */
public class RawXMLPayload extends Payload {
    private String rawXML_;

    public void setRawXML(String data) {
        rawXML_ = data;
    }

    public String getRawXML() {
        return rawXML_;
    }
    
    @Override
    public String toString() {
        return "RawXMLPayload : " + rawXML_ == null ? "<null>" : rawXML_;
    }
}
