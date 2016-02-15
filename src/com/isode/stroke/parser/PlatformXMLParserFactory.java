/*
 * Copyright (c) 2010-2016, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.parser;


public class PlatformXMLParserFactory extends XMLParserFactory {

    public static XMLParser createXMLParser(XMLParserClient client) {
        return new AaltoXMLParser(client);
    }

    @Override
    public XMLParser createParser(XMLParserClient xmlParserClient) {
        return createXMLParser(xmlParserClient);
    }
}
