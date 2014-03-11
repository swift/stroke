/*
* Copyright (c) 2014 Kevin Smith and Remko Tronçon
* All rights reserved.
*/

/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/

package com.isode.stroke.serializer.payloadserializers;

import org.junit.Test;
import com.isode.stroke.base.DateTime;
import com.isode.stroke.elements.Delay;
import com.isode.stroke.elements.Forwarded;
import com.isode.stroke.elements.MAMResult;
import com.isode.stroke.elements.Message;
import com.isode.stroke.jid.JID;
import static org.junit.Assert.assertEquals;

public class MAMResultSerializerTest {

    @Test
    public void testSerialize() {
        MAMResultSerializer serializer = new MAMResultSerializer(serializers_);
    
        Message message = new Message();
        message.setType(Message.Type.Chat);
        message.setTo(JID.fromString("juliet@capulet.lit/balcony"));
        message.setFrom(JID.fromString("romeo@montague.lit/orchard"));
        message.setBody("Call me but love, and I'll be new baptized; Henceforth I never will be Romeo.");
    
        Forwarded forwarded = new Forwarded();
        forwarded.setStanza(message);
        forwarded.setDelay(new Delay(DateTime.stringToDate("2010-07-10T23:08:25Z"), null));
    
        MAMResult result = new MAMResult();
        result.setID("28482-98726-73623");
        result.setQueryID("f27");
        result.setPayload(forwarded);
    
        String expectedResult = 
            "<result id=\"28482-98726-73623\" queryid=\"f27\" xmlns=\"urn:xmpp:mam:0\">"
          +     "<forwarded xmlns=\"urn:xmpp:forward:0\">"
          +         "<delay stamp=\"2010-07-10T23:08:25Z\" xmlns=\"urn:xmpp:delay\"/>"
          +         "<message from=\"romeo@montague.lit/orchard\" to=\"juliet@capulet.lit/balcony\" type=\"chat\">"
          +             "<body>Call me but love, and I'll be new baptized; Henceforth I never will be Romeo.</body>"
          +         "</message>"
          +     "</forwarded>"
          + "</result>";
    
        assertEquals(expectedResult, serializer.serialize(result));
    }

    FullPayloadSerializerCollection serializers_ = new FullPayloadSerializerCollection();
}
