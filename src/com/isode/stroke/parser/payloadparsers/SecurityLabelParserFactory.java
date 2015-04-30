/*
 * Copyright (c) 2010-2015 Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.parser.GenericPayloadParserFactory;

public class SecurityLabelParserFactory extends GenericPayloadParserFactory<SecurityLabelParser> {

    public SecurityLabelParserFactory() {
        super("securitylabel", "urn:xmpp:sec-label:0", SecurityLabelParser.class);
    }

}
