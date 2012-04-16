/*
 * Copyright (c) 2012 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon
 * All rights reserved.
 */ 
package com.isode.stroke.parser.payloadparsers;

import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.eventloop.DummyEventLoop;
import static org.junit.Assert.*;

import org.junit.Test;


/**
 * Junit tests for the Error Parser
 *
 */
public class ErrorParserTest {

    /**
     * Parse the xml string representing containing the error payload 
     * @param xmlString XML string not null
     * @return Error Payload, not null
     */
    private static ErrorPayload parse(String xmlString) {
        DummyEventLoop eventLoop = new DummyEventLoop();
        PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
        assertTrue(parser.parse(xmlString));

        Payload payload = null;
        do {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            eventLoop.processEvents();
            payload = parser.getPayload();
        } while (payload == null);

        return (ErrorPayload) payload;
    }

    @Test
    public void testParse() {
        ErrorPayload payload = parse(
                "<error type=\"modify\">"+
                "<bad-request xmlns=\"urn:ietf:params:xml:ns:xmpp-stanzas\"/>"+
                "<text xmlns=\"urn:ietf:params:xml:ns:xmpp-stanzas\">boo</text>"+
        "</error>");
        assertEquals(ErrorPayload.Condition.BadRequest, payload.getCondition());
        assertEquals(ErrorPayload.Type.Modify, payload.getType());
        assertEquals("boo", payload.getText());
        assertTrue(payload.getPayload() == null);
    }

    @Test
    public void testParseWithPayload() {
        ErrorPayload payload = parse(
                "<error type=\"modify\">"+
                "<bad-request xmlns=\"urn:ietf:params:xml:ns:xmpp-stanzas\"/>"+
                "<delay xmlns='urn:xmpp:delay' from='juliet@capulet.com/balcony' stamp='2002-09-10T23:41:07Z'/>"+
                "<text xmlns=\"urn:ietf:params:xml:ns:xmpp-stanzas\">boo</text>"+
        "</error>");
        assertEquals(ErrorPayload.Condition.BadRequest, payload.getCondition());
        assertEquals(ErrorPayload.Type.Modify, payload.getType());
        assertEquals("boo", payload.getText());
        //TODO After porting Delay, this line should be uncommented
        //assertTrue(payload.getPayload() instanceof Delay);
    }

}
