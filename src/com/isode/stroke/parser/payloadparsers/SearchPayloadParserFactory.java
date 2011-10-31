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
import com.isode.stroke.parser.PayloadParser;

public class SearchPayloadParserFactory extends GenericPayloadParserFactory<SearchPayloadParser> {

    public SearchPayloadParserFactory() {
        super("query", "jabber:iq:search", SearchPayloadParser.class);
    }

}
