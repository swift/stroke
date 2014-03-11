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
import com.isode.stroke.elements.IQ;
import com.isode.stroke.elements.Message;
import com.isode.stroke.elements.Presence;
import com.isode.stroke.eventloop.DummyEventLoop;

public class ForwardedParserTest {

    @Test
    public void testParseIQ() {
        DummyEventLoop eventLoop = new DummyEventLoop();
        PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
        assertTrue(parser.parse(
            "<forwarded xmlns=\"urn:xmpp:forward:0\">"
          +     "<delay xmlns=\"urn:xmpp:delay\" stamp=\"2010-07-10T23:08:25Z\"/>"
          +     "<iq type=\"get\" from=\"kindanormal@example.com/IM\" to=\"stupidnewbie@example.com\" id=\"id0\"/>"
          + "</forwarded>"));

        assertTrue(parser.getPayload() instanceof Forwarded);
        Forwarded payload = (Forwarded)parser.getPayload();
        assertTrue(payload != null);
        assertTrue(payload.getDelay() != null);
        assertEquals("2010-07-10T23:08:25Z", DateTime.dateToString(payload.getDelay().getStamp()));

        assertTrue(payload.getStanza() instanceof IQ);
        IQ iq = (IQ)payload.getStanza();
        assertTrue(iq != null);
        assertEquals("stupidnewbie@example.com", iq.getTo().toString());
        assertEquals("kindanormal@example.com/IM", iq.getFrom().toString());
        assertEquals("id0", iq.getID());
        assertEquals(IQ.Type.Get, iq.getType());
    }

    @Test
    public void testParseMessage() {
        DummyEventLoop eventLoop = new DummyEventLoop();
        PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
        assertTrue(parser.parse(
            "<forwarded xmlns=\"urn:xmpp:forward:0\">"
          +     "<delay xmlns=\"urn:xmpp:delay\" stamp=\"2010-07-10T23:08:25Z\"/>"
          +     "<message xmlns=\"jabber:client\" to=\"juliet@capulet.lit/balcony\" from=\"romeo@montague.lit/orchard\" type=\"chat\">"
          +         "<body>Call me but love, and I'll be new baptized; Henceforth I never will be Romeo.</body>"
          +     "</message>"
          + "</forwarded>"));

        assertTrue(parser.getPayload() instanceof Forwarded);
        Forwarded payload = (Forwarded)parser.getPayload();
        assertTrue(payload != null);
        assertTrue(payload.getDelay() != null);
        assertEquals("2010-07-10T23:08:25Z", DateTime.dateToString(payload.getDelay().getStamp()));

        assertTrue(payload.getStanza() instanceof Message);
        Message message = (Message)payload.getStanza();
        assertTrue(message != null);
        String expectedBody = "Call me but love, and I'll be new baptized; Henceforth I never will be Romeo.";
        assertEquals(expectedBody, message.getBody());
        assertEquals(Message.Type.Chat, message.getType());
        assertEquals("juliet@capulet.lit/balcony", message.getTo().toString());
        assertEquals("romeo@montague.lit/orchard", message.getFrom().toString());
    }

    @Test
    public void testParseMessageNoDelay() {
        DummyEventLoop eventLoop = new DummyEventLoop();
        PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
        assertTrue(parser.parse(
            "<forwarded xmlns=\"urn:xmpp:forward:0\">"
          +     "<message xmlns=\"jabber:client\" to=\"juliet@capulet.lit/balcony\" from=\"romeo@montague.lit/orchard\" type=\"chat\">"
          +         "<body>Call me but love, and I'll be new baptized; Henceforth I never will be Romeo.</body>"
          +     "</message>"
          + "</forwarded>"));

        assertTrue(parser.getPayload() instanceof Forwarded);
        Forwarded payload = (Forwarded)parser.getPayload();
        assertTrue(payload != null);
        assertTrue(payload.getDelay() == null);

        assertTrue(payload.getStanza() instanceof Message);
        Message message = (Message)payload.getStanza();
        assertTrue(message != null);
        String expectedBody = "Call me but love, and I'll be new baptized; Henceforth I never will be Romeo.";
        assertEquals(expectedBody, message.getBody());
        assertEquals(Message.Type.Chat, message.getType());
        assertEquals("juliet@capulet.lit/balcony", message.getTo().toString());
        assertEquals("romeo@montague.lit/orchard", message.getFrom().toString());
    }

    @Test
    public void testParsePresence() {
        DummyEventLoop eventLoop = new DummyEventLoop();
        PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
        assertTrue(parser.parse(
            "<forwarded xmlns=\"urn:xmpp:forward:0\">"
          +     "<delay xmlns=\"urn:xmpp:delay\" stamp=\"2010-07-10T23:08:25Z\"/>"
          +     "<presence from=\"alice@wonderland.lit/rabbithole\" to=\"madhatter@wonderland.lit\" type=\"unavailable\"/>"
          + "</forwarded>"));

        assertTrue(parser.getPayload() instanceof Forwarded);
        Forwarded payload = (Forwarded)parser.getPayload();
        assertTrue(payload != null);
        assertTrue(payload.getDelay() != null);
        assertEquals("2010-07-10T23:08:25Z", DateTime.dateToString(payload.getDelay().getStamp()));

        assertTrue(payload.getStanza() instanceof Presence);
        Presence presence = (Presence)payload.getStanza();
        assertTrue(presence != null);
        assertEquals("madhatter@wonderland.lit", presence.getTo().toString());
        assertEquals("alice@wonderland.lit/rabbithole", presence.getFrom().toString());
        assertEquals(Presence.Type.Unavailable, presence.getType());
    }
}
