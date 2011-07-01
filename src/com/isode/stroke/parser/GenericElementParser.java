/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.parser;

import com.isode.stroke.elements.Element;

public class GenericElementParser<T extends Element> implements ElementParser {

    private final T element_;

    public GenericElementParser(Class c) {
        try {
            element_ =  (T) c.newInstance();
        } catch (InstantiationException ex) {
            /* Fatal */
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            /* Fatal */
            throw new RuntimeException(ex);
        }
    }


    public Element getElement() {
        return element_;
    }

    protected T getElementGeneric() {
        return element_;
    }

    public void handleStartElement(String a, String b, AttributeMap c) {
    }

    public void handleEndElement(String a, String b) {
    }

    public void handleCharacterData(String a) {
    }
}
