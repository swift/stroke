/*
 * Copyright (c) 2012 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon
 * All rights reserved.
 */

package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.Payload;
import com.isode.stroke.eventloop.EventLoop;
import com.isode.stroke.parser.AttributeMap;
import com.isode.stroke.parser.PayloadParser;
import com.isode.stroke.parser.PayloadParserFactory;
import com.isode.stroke.parser.PlatformXMLParserFactory;
import com.isode.stroke.parser.XMLParser;
import com.isode.stroke.parser.XMLParserClient;

public class PayloadsParserTester implements XMLParserClient {
    private XMLParser xmlParser;
    private FullPayloadParserFactoryCollection factories = new FullPayloadParserFactoryCollection();
    private PayloadParser payloadParser;
    private int level;

    public PayloadsParserTester(EventLoop eventLoop) {
        level = 0;
        xmlParser = PlatformXMLParserFactory.createXMLParser(this);
    }

    public boolean parse(String data) {
        return xmlParser.parse(data);
    }

    public void handleStartElement(String element, String ns,
            AttributeMap attributes) {
        if (level == 0) {
            assert (payloadParser == null);
            PayloadParserFactory payloadParserFactory = factories
                    .getPayloadParserFactory(element, ns, attributes);
            assert (payloadParserFactory != null);
            payloadParser = payloadParserFactory.createPayloadParser();
        }
        payloadParser.handleStartElement(element, ns, attributes);
        level++;
    }

    public void handleEndElement(String element, String ns) {
        level--;
        payloadParser.handleEndElement(element, ns);
    }

    public void handleCharacterData(String data) {
        payloadParser.handleCharacterData(data);
    }

    public Payload getPayload() {
        if (payloadParser == null) {
            return null;
        }
        return payloadParser.getPayload();
    }
}
