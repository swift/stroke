/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.serializer.xml;

public class XMLTextNode implements XMLNode {

    private final String text_;

    public XMLTextNode(String text) {
        text_ = text;
        text_.replaceAll("&", "&amp;"); // Should come first
        text_.replaceAll("<", "&lt;");
        text_.replaceAll(">", "&gt;");
    }

    public String serialize() {
        return text_;
    }
}
