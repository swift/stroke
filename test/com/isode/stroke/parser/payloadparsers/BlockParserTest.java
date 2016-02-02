/*  Copyright (c) 2016, Isode Limited, London, England.
 *  All rights reserved.
 *
 *  Acquisition and use of this software and related materials for any
 *  purpose requires a written license agreement from Isode Limited,
 *  or a written license from an organisation licensed by Isode Limited
 *  to grant such a license.
 *
 */
package com.isode.stroke.parser.payloadparsers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.isode.stroke.elements.BlockListPayload;
import com.isode.stroke.elements.BlockPayload;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.elements.UnblockPayload;
import com.isode.stroke.eventloop.DummyEventLoop;
import com.isode.stroke.jid.JID;

/**
 * Test for {@link BlockParser} (and its subclasses)
 */
public class BlockParserTest {

    /**
     *  Constructor
     */
    public BlockParserTest() {
        
    }
    
    @Test
    public void testExample4() {
        PayloadsParserTester parser = 
                new PayloadsParserTester(new DummyEventLoop());
        boolean success = parser.parse("<blocklist xmlns='urn:xmpp:blocking'>"
                        + "<item jid='romeo@montague.net'/>"
                        + "<item jid='iago@shakespeare.lit'/>"
                    + "</blocklist>");
        assertTrue(success);
        Payload payload = parser.getPayload();
        assertTrue(payload instanceof BlockListPayload);
        BlockListPayload blockListPayload = (BlockListPayload) payload;
        assertEquals(2,blockListPayload.getItems().size());
        assertEquals(new JID("romeo@montague.net"),blockListPayload.getItems().get(0));
        assertEquals(new JID("iago@shakespeare.lit"),blockListPayload.getItems().get(1));
    }
    
    @Test
    public void testExample6() {
        PayloadsParserTester parser =
                new PayloadsParserTester(new DummyEventLoop());
        boolean success = parser.parse("<block xmlns='urn:xmpp:blocking'>"
                                        +"<item jid='romeo@montague.net'/>"
                                      +"</block>");
        assertTrue(success);
        Payload payload = parser.getPayload();
        assertTrue(payload instanceof BlockPayload);
        BlockPayload blockPayload = (BlockPayload) payload;
        assertEquals(1,blockPayload.getItems().size());
        assertEquals(new JID("romeo@montague.net"),blockPayload.getItems().get(0));
    }
    
    @Test
    public void testExample10() {
        PayloadsParserTester parser =
                new PayloadsParserTester(new DummyEventLoop());
        boolean success = parser.parse("<unblock xmlns='urn:xmpp:blocking'>"
                                        +"<item jid='romeo@montague.net'/>"
                                      +"</unblock>");
        assertTrue(success);
        Payload payload = parser.getPayload();
        assertTrue(payload instanceof UnblockPayload);
        UnblockPayload unblockPayload = (UnblockPayload) payload;
        assertEquals(1,unblockPayload.getItems().size());
        assertEquals(new JID("romeo@montague.net"),unblockPayload.getItems().get(0));
    }

}
