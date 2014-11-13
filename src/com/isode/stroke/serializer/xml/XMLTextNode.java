/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.serializer.xml;

public class XMLTextNode implements XMLNode {

    private String text_;

    public XMLTextNode(String text) {
        text_ = text != null ? text : "";
        text_ = text_.replaceAll("&", "&amp;"); // Should come first
        text_ = text_.replaceAll("<", "&lt;");
        text_ = text_.replaceAll(">", "&gt;");
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
        if (text == null) {
            throw new NullPointerException("'text' must not be null");
        }

        return new XMLTextNode(text);
    }
}
