/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.parser;

import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.serializer.xml.XMLTextNode;
import java.util.Vector;

public class SerializingParser {

    private final Vector<XMLElement> elementStack_ = new Vector<XMLElement>();
    private XMLElement rootElement_;

    public void handleStartElement(String tag, String ns, AttributeMap attributes) {
        XMLElement element = new XMLElement(tag, ns);
        for (String name : attributes.keySet()) {
            element.setAttribute(name, attributes.get(name));
        }

        if (elementStack_.isEmpty()) {
            rootElement_ = element;
        } else {
            elementStack_.lastElement().addNode(element);
        }
        elementStack_.add(element);
    }

    public void handleEndElement(String tag, String ns) {
        assert (!elementStack_.isEmpty());
        elementStack_.remove(elementStack_.size() - 1);
    }

    public void handleCharacterData(String data) {
        if (!elementStack_.isEmpty()) {
            elementStack_.lastElement().addNode(new XMLTextNode(data));
        }
    }

    public String getResult() {
        return (rootElement_ != null ? rootElement_.serialize() : "");
    }
}
