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
import com.isode.stroke.elements.MUCDestroyPayload;
import com.isode.stroke.elements.MUCItem;
import com.isode.stroke.elements.MUCOccupant;
import com.isode.stroke.elements.MUCUserPayload;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.eventloop.DummyEventLoop;
import com.isode.stroke.jid.JID;

public class MUCUserPayloadParserTest {
    
    private static MUCUserPayload parse(String xmlString) {
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

        return (MUCUserPayload) payload;
    }

    @Test
    public void testParse() {
        boolean found110 = false;
        boolean found210 = false;

        MUCUserPayload payload = parse("<x xmlns=\"http://jabber.org/protocol/muc#user\"><status code='110'/>" +
                "<item affiliation=\"owner\" role=\"visitor\"><actor jid=\"kev@tester.lit\"/>" +
        "<reason>malice</reason></item><status code='210'/></x>");

        for (MUCUserPayload.StatusCode status : payload.getStatusCodes()) {
            if (status.code == 110) found110 = true;
            if (status.code == 210) found210 = true;
        }

        MUCItem item = payload.getItems().get(0);
        assertEquals(MUCOccupant.Affiliation.Owner, item.affiliation);
        assertEquals(MUCOccupant.Role.Visitor, item.role);
        assertEquals(new JID("kev@tester.lit"), item.actor);
        assertEquals("malice", item.reason);
        assertTrue(found110);
        assertTrue(found210);
    }

    @Test
    public void testParseEmpty() {        
        MUCUserPayload payload = parse("<x xmlns=\"http://jabber.org/protocol/muc#user\">" +
    "<destroy jid='alice@wonderland.lit'><reason>bert</reason></destroy></x>");
        assertTrue(payload != null);
        assertTrue(payload.getItems().isEmpty());
    }
    
    @Test
    public void testParseDestroy() {
        MUCUserPayload payload = parse("<x xmlns=\"http://jabber.org/protocol/muc#user\">" +
        "<destroy jid='alice@wonderland.lit'><reason>bert</reason></destroy></x>");
        assertTrue(payload!= null);
        MUCDestroyPayload destroy = (MUCDestroyPayload)(payload.getPayload());
        assertTrue(destroy != null);
        assertEquals("bert", destroy.getReason());
        assertEquals(new JID("alice@wonderland.lit"), destroy.getNewVenue());
    }

    @Test
    public void testParseInvite() {
        MUCUserPayload payload = parse("<x xmlns=\"http://jabber.org/protocol/muc#user\">" +
                "<invite from='crone1@shakespeare.lit/desktop' to='alice@wonderland.lit/xxx'>      " +
                "<reason>Hey Hecate, this is the place for all good witches!</reason>    " +
        "</invite>    <password>cauldronburn</password></x>");
        assertTrue(payload != null);
        assertTrue(payload.getInvite() != null);
        assertTrue(payload.getPassword() != null);
        assertEquals("cauldronburn", payload.getPassword());
        MUCUserPayload.Invite invite = payload.getInvite();
        assertEquals("Hey Hecate, this is the place for all good witches!", invite.reason);
        assertEquals(new JID("crone1@shakespeare.lit/desktop"), invite.from);
        assertEquals(new JID("alice@wonderland.lit/xxx"), invite.to);
    }

}
