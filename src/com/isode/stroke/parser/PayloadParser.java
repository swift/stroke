/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.parser;

import com.isode.stroke.elements.Payload;

/**
 * Parse a payload.
 */
public interface PayloadParser {

    void handleStartElement(String element, String ns, AttributeMap attributes);

    void handleEndElement(String element, String ns);

    void handleCharacterData(String data);

    Payload getPayload();
}
