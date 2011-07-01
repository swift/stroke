/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.serializer.xml;

public class XMLRawTextNode implements XMLNode {
    private final String text_;

    public XMLRawTextNode(String text) {
        text_ = text;
    }

    public String serialize() {
        return text_;
    }
}
