/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.parser;

import com.isode.stroke.eventloop.EventLoop;

class PlatformXMLParserFactory {
    /**
     * Unlike Swiften, this may be threaded, and therefore needs an eventloop.
     */
    public static XMLParser createXMLParser(XMLParserClient client, EventLoop eventLoop) {
        return new PullXMLParser(client, eventLoop);
    }
}
