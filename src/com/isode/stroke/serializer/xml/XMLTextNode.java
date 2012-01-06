/*
 * Copyright (c) 2010-2012, Isode Limited, London, England.
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

    /**
     * Create new object.
     * 
     * @param text Text to create object with, must not be null
     * 
     * @return new object, will never be null
     */
    public static XMLTextNode create(String text) {
        return new XMLTextNode(text);
    }
}
