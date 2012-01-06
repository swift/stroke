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
        String result = "";
        result += "<" + tag_;
        for (String key : attributes_.keySet()) {
            result += " " + key + "=\"" + attributes_.get(key) + "\"";
        }

        if (childNodes_.size() > 0) {
            result += ">";
            for (XMLNode node : childNodes_) {
                result += node.serialize();
            }
            result += "</" + tag_ + ">";
        } else {
            result += "/>";
        }
        return result;
    }

    public void setAttribute(String attribute, String value) {
        String escapedValue = value;
        escapedValue.replaceAll("&", "&amp;");
        escapedValue.replaceAll("<", "&lt;");
        escapedValue.replaceAll(">", "&gt;");
        escapedValue.replaceAll("'", "&apos;");
        escapedValue.replaceAll("\"", "&quot;");
        attributes_.put(attribute, escapedValue);
    }

    public void addNode(XMLNode node) {
        childNodes_.add(node);
    }
}
