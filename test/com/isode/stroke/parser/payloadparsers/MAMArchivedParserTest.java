/*
* Copyright (c) 2014 Kevin Smith and Remko Tronçon
* All rights reserved.
*/

/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/

package com.isode.stroke.parser.payloadparsers;

import org.junit.Test;
import com.isode.stroke.elements.MAMArchived;
import com.isode.stroke.eventloop.DummyEventLoop;
import com.isode.stroke.jid.JID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MAMArchivedParserTest {

    @Test
    public void testParse() {
        DummyEventLoop eventLoop = new DummyEventLoop();
        PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
        assertTrue(parser.parse(
            "<archived by=\"juliet@capulet.lit\" id=\"28482-98726-73623\" xmlns=\"urn:xmpp:mam:0\"/>"));

        MAMArchived payload = (MAMArchived)parser.getPayload();
        assertTrue(payload != null);
        assertEquals(JID.fromString("juliet@capulet.lit"), payload.getBy());
        assertEquals("28482-98726-73623", payload.getID());
    }
}
