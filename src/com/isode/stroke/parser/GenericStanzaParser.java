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
import com.isode.stroke.elements.Stanza;

public class GenericStanzaParser<T extends Stanza> extends StanzaParser {
    private final T stanza_;
    public GenericStanzaParser(PayloadParserFactoryCollection collection, T blankStanza) {
        super(collection);
        stanza_ = blankStanza;
    }

    public Element getElement() {
        return stanza_;
    }

    public T getStanzaGeneric() {
        return stanza_;
    }
}
