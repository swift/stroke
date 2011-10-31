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

/**
 * Parse XML Elements.
 */
public interface ElementParser {

    void handleStartElement(String element, String ns, AttributeMap attributes);

    void handleEndElement(String element, String ns);

    void handleCharacterData(String data);

    Element getElement();
}
