/*
 * Copyright (c) 2010, 2011 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.parser.GenericPayloadParserFactory;

public class BodyParserFactory extends GenericPayloadParserFactory<BodyParser> {

    public BodyParserFactory() {
        super("body", BodyParser.class);
    }

}
