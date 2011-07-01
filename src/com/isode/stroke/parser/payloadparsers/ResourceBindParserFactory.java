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

class ResourceBindParserFactory extends GenericPayloadParserFactory<ResourceBindParser> {

    public ResourceBindParserFactory() {
        super("bind", "urn:ietf:params:xml:ns:xmpp-bind", ResourceBindParser.class);
    }

}
