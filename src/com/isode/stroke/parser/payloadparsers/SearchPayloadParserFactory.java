/*
 * Copyright (c) 2010-2015 Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.parser.GenericPayloadParserFactory;

public class SearchPayloadParserFactory extends GenericPayloadParserFactory<SearchPayloadParser> {

    public SearchPayloadParserFactory() {
        super("query", "jabber:iq:search", SearchPayloadParser.class);
    }

}
