/*
 * Copyright (c) 2010-2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.parser;

import com.isode.stroke.eventloop.EventLoop;

public class PlatformXMLParserFactory {
    /**
     * Unlike Swiften, this may be threaded, and therefore needs an eventloop.
     */
    public static XMLParser createXMLParser(XMLParserClient client, EventLoop eventLoop) {
        return new AaltoXMLParser(client, eventLoop);
    }
}
