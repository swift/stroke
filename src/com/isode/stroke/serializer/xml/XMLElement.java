/*
 * Copyright (c) 2010-2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.serializer.xml;

import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

public class XMLElement implements XMLNode {

    private final String tag_;
    private final Map<String, String> attributes_ = new TreeMap<String, String>();
    private final Vector<XMLNode> childNodes_ = new Vector<XMLNode>();

    public XMLElement(String tag) {
        this(tag, "");
    }

    public XMLElement(String tag, String xmlns) {
        tag_ = tag;
        if (xmlns.length()!=0) {
            setAttribute("xmlns", xmlns);
        }
    }

    public XMLElement(String tag, String xmlns, String text) {
        this(tag, xmlns);
        if (text.length() > 0) {
            addNode(new XMLTextNode(text));
        }
    }

    public String serialize() {
        StringBuilder result = new StringBuilder();
        result.append("<").append(tag_);
        for (String key : attributes_.keySet()) {
            result.append(" ").append(key).append("=\"").append(attributes_.get(key)).append("\"");
        }

        if (childNodes_.size() > 0) {
            result.append(">");
            for (XMLNode node : childNodes_) {
                result.append(node.serialize());
            }
            result.append("</").append(tag_).append(">");
        } else {
            result.append("/>");
        }
        return result.toString();
    }

    public void setAttribute(String attribute, String value) {
        String escapedValue = value;
        escapedValue = escapedValue.replaceAll("&", "&amp;");
        escapedValue = escapedValue.replaceAll("<", "&lt;");
        escapedValue = escapedValue.replaceAll(">", "&gt;");
        escapedValue = escapedValue.replaceAll("'", "&apos;");
        escapedValue = escapedValue.replaceAll("\"", "&quot;");
        attributes_.put(attribute, escapedValue);
    }

    public void addNode(XMLNode node) {
        childNodes_.add(node);
    }
}
