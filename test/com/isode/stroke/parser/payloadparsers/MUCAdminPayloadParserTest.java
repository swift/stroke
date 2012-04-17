/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2011, Kevin Smith
 * All rights reserved.
 */
package com.isode.stroke.parser.payloadparsers;

import static org.junit.Assert.*;

import org.junit.Test;

import com.isode.stroke.elements.MUCAdminPayload;
import com.isode.stroke.elements.MUCItem;
import com.isode.stroke.elements.MUCOccupant;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.eventloop.DummyEventLoop;
import com.isode.stroke.jid.JID;

public class MUCAdminPayloadParserTest {
    private static MUCAdminPayload parse(String xmlString) {
        DummyEventLoop eventLoop = new DummyEventLoop();
        PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
        assertTrue(parser.parse(xmlString));

        Payload payload = null;
        do {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            eventLoop.processEvents();
            payload = parser.getPayload();
        } while (payload == null);

        return (MUCAdminPayload) payload;
    }

    @Test
    public void testParse() throws Exception{
        MUCAdminPayload payload = (parse("<query xmlns=\"http://jabber.org/protocol/muc#admin\">" +
                "<item affiliation=\"owner\" role=\"visitor\">" +
        "<actor jid=\"kev@tester.lit\"/><reason>malice</reason></item></query>"));
        MUCItem item = payload.getItems().get(0);
        assertEquals(MUCOccupant.Affiliation.Owner, item.affiliation);
        assertEquals(MUCOccupant.Role.Visitor, item.role);
        assertEquals(new JID("kev@tester.lit"), item.actor);
        assertEquals("malice", item.reason);
    }
}
