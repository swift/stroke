/*
* Copyright (c) 2014-2015, Isode Limited, London, England.
* All rights reserved.
*/

package com.isode.stroke.serializer.payloadserializers;

import org.junit.Test;

import com.isode.stroke.base.DateTime;
import com.isode.stroke.elements.Delay;
import com.isode.stroke.elements.Forwarded;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.elements.Message;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.elements.Presence;
import com.isode.stroke.elements.Subject;
import com.isode.stroke.jid.JID;

import static org.junit.Assert.assertEquals;

public class ForwardedSerializerTest {

    @Test
    public void testSerializeIQ() {
        ForwardedSerializer serializer = new ForwardedSerializer(serializers_);

        IQ iq = IQ.createResult(JID.fromString("juliet@capulet.lit/balcony"), JID.fromString("romeo@montague.lit/orchard"), "id0", new Subject("text"));

        Forwarded forwarded = new Forwarded();
        forwarded.setStanza(iq);
        forwarded.setDelay(new Delay(DateTime.stringToDate("2010-07-10T23:08:25Z"), null));

        String expectedResult = 
            "<forwarded xmlns=\"urn:xmpp:forward:0\">"
          +     "<delay stamp=\"2010-07-10T23:08:25Z\" xmlns=\"urn:xmpp:delay\"/>"
          +     "<iq from=\"romeo@montague.lit/orchard\" id=\"id0\" to=\"juliet@capulet.lit/balcony\" type=\"result\"><subject>text</subject></iq>"
          + "</forwarded>";

        assertEquals(expectedResult, serializer.serialize(forwarded));
    }

    @Test
    public void testSerializeMessage() {
        ForwardedSerializer serializer = new ForwardedSerializer(serializers_);

        Message message = new Message();
        message.setType(Message.Type.Chat);
        message.setTo(JID.fromString("juliet@capulet.lit/balcony"));
        message.setFrom(JID.fromString("romeo@montague.lit/orchard"));
        message.setBody("Call me but love, and I'll be new baptized; Henceforth I never will be Romeo.");

        Forwarded forwarded = new Forwarded();
        forwarded.setStanza(message);
        forwarded.setDelay(new Delay(DateTime.stringToDate("2010-07-10T23:08:25Z"), null));

        String expectedResult = 
            "<forwarded xmlns=\"urn:xmpp:forward:0\">"
          +     "<delay stamp=\"2010-07-10T23:08:25Z\" xmlns=\"urn:xmpp:delay\"/>"
          +     "<message from=\"romeo@montague.lit/orchard\" to=\"juliet@capulet.lit/balcony\" type=\"chat\">"
          +         "<body>Call me but love, and I'll be new baptized; Henceforth I never will be Romeo.</body>"
          +     "</message>"
          + "</forwarded>";

        assertEquals(expectedResult, serializer.serialize(forwarded));
    }

    @Test
    public void testSerializeMessageNoDelay() {
        ForwardedSerializer serializer = new ForwardedSerializer(serializers_);

        Message message = new Message();
        message.setType(Message.Type.Chat);
        message.setTo(JID.fromString("juliet@capulet.lit/balcony"));
        message.setFrom(JID.fromString("romeo@montague.lit/orchard"));
        message.setBody("Call me but love, and I'll be new baptized; Henceforth I never will be Romeo.");

        Forwarded forwarded = new Forwarded();
        forwarded.setStanza(message);

        String expectedResult = 
            "<forwarded xmlns=\"urn:xmpp:forward:0\">"
          +     "<message from=\"romeo@montague.lit/orchard\" to=\"juliet@capulet.lit/balcony\" type=\"chat\">"
          +         "<body>Call me but love, and I'll be new baptized; Henceforth I never will be Romeo.</body>"
          +     "</message>"
          + "</forwarded>";

        assertEquals(expectedResult, serializer.serialize(forwarded));
    }

    @Test
    public void testSerializePresence() {
        ForwardedSerializer serializer = new ForwardedSerializer(serializers_);

        Presence presence = new Presence();
        presence.setType(Presence.Type.Subscribe);

        Forwarded forwarded = new Forwarded();
        forwarded.setStanza(presence);
        forwarded.setDelay(new Delay(DateTime.stringToDate("2010-07-10T23:08:25Z"), null));

        String expectedResult = 
            "<forwarded xmlns=\"urn:xmpp:forward:0\">"
          +     "<delay stamp=\"2010-07-10T23:08:25Z\" xmlns=\"urn:xmpp:delay\"/>"
          +     "<presence type=\"subscribe\"/>"
          + "</forwarded>";

        assertEquals(expectedResult, serializer.serialize(forwarded));
    }

    FullPayloadSerializerCollection serializers_ = new FullPayloadSerializerCollection();
}
