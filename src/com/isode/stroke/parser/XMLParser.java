/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.parser;

public abstract class XMLParser {
    private final XMLParserClient client_;

    public XMLParser(XMLParserClient client) {
        client_ = client;
    }

    public abstract boolean parse(String data);

    protected XMLParserClient getClient() {
        return client_;
    }
}
