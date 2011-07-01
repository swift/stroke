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

public class RosterParserFactory extends GenericPayloadParserFactory<RosterParser> {

    public RosterParserFactory() {
        super("query", "jabber:iq:roster", RosterParser.class);
    }

}
