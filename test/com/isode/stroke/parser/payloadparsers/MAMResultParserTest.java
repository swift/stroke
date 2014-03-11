/*
* Copyright (c) 2014 Kevin Smith and Remko Tronçon
* All rights reserved.
*/

/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/

package com.isode.stroke.parser.payloadparsers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import com.isode.stroke.base.DateTime;
import com.isode.stroke.elements.Forwarded;
import com.isode.stroke.elements.MAMResult;
import com.isode.stroke.elements.Message;
import com.isode.stroke.eventloop.DummyEventLoop;

public class MAMResultParserTest {

    @Test
    public void testParse() {
        DummyEventLoop eventLoop = new DummyEventLoop();
        PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
        assertTrue(parser.parse(
            "<result id=\"28482-98726-73623\" queryid=\"f27\" xmlns=\"urn:xmpp:mam:0\">"
          +     "<forwarded xmlns=\"urn:xmpp:forward:0\">"
          +         "<delay stamp=\"2010-07-10T23:08:25Z\" xmlns=\"urn:xmpp:delay\"/>"
          +         "<message from=\"romeo@montague.lit/orchard\" to=\"juliet@capulet.lit/balcony\" type=\"chat\">"
          +             "<body>Call me but love, and I'll be new baptized; Henceforth I never will be Romeo.</body>"
          +         "</message>"
          +     "</forwarded>"
          + "</result>"));

        MAMResult payload = (MAMResult)parser.getPayload();
        assertTrue(payload != null);
        assertTrue(payload.getID() != null);
        assertEquals("28482-98726-73623", payload.getID());
        assertTrue(payload.getQueryID() != null);
        assertEquals("f27", payload.getQueryID());

        Forwarded forwarded = payload.getPayload();
        assertTrue(forwarded.getDelay() != null);
        assertEquals("2010-07-10T23:08:25Z", DateTime.dateToString(forwarded.getDelay().getStamp()));

        assertTrue(forwarded.getStanza() instanceof Message);
        Message message = (Message)forwarded.getStanza();
        assertTrue(message != null);
        String expectedBody = "Call me but love, and I'll be new baptized; Henceforth I never will be Romeo.";
        assertEquals(expectedBody, message.getBody());
        assertEquals(Message.Type.Chat, message.getType());
        assertEquals("juliet@capulet.lit/balcony", message.getTo().toString());
        assertEquals("romeo@montague.lit/orchard", message.getFrom().toString());
    }
}
