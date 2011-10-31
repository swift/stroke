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

class StartSessionParserFactory extends GenericPayloadParserFactory<StartSessionParser> {

    public StartSessionParserFactory() {
        super("session", "urn:ietf:params:xml:ns:xmpp-session", StartSessionParser.class);
    }

}
