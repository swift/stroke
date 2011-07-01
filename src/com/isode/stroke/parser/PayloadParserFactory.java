/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.parser;

/**
 * Create a parser.
 */
public interface PayloadParserFactory {
    boolean canParse(String element, String ns, AttributeMap attributes);
    PayloadParser createPayloadParser();
}
