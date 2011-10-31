/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.parser;

import com.isode.stroke.elements.Element;
import com.isode.stroke.elements.ProtocolHeader;

public interface XMPPParserClient {
    void handleStreamStart(ProtocolHeader header);
    void handleElement(Element element);
    void handleStreamEnd();
}
