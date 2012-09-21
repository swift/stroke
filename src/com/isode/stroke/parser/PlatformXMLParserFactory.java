/*
 * Copyright (c) 2010-2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.parser;


public class PlatformXMLParserFactory {

    public static XMLParser createXMLParser(XMLParserClient client) {
        return new AaltoXMLParser(client);
    }
}
