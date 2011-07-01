/*
 * Copyright (c) 2010 Remko Tronçon
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, 2011 Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.parser.GenericPayloadParserFactory;

public class SoftwareVersionParserFactory extends GenericPayloadParserFactory<SoftwareVersionParser> {

    public SoftwareVersionParserFactory() {
        super("query", "jabber:iq:version", SoftwareVersionParser.class);
    }

}
