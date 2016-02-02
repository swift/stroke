/*  Copyright (c) 2016, Isode Limited, London, England.
 *  All rights reserved.
 *                                                                       
 *  Acquisition and use of this software and related materials for any      
 *  purpose requires a written license agreement from Isode Limited,
 *  or a written license from an organisation licensed by Isode Limited
 *  to grant such a license.
 *
 */
package com.isode.stroke.serializer.payloadserializers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.isode.stroke.elements.BlockListPayload;
import com.isode.stroke.elements.BlockPayload;
import com.isode.stroke.elements.UnblockPayload;
import com.isode.stroke.jid.JID;

/**
 * Test for {@link BlockSerializer} classes
 */
public class BlockSerializerTest {

    /**
     * Constructor
     */
    public BlockSerializerTest() {
        // Empty Constructor
    }
    
    @Test
    public void testExample4() {
        BlockBlockListPayloadSerializer testling = new BlockBlockListPayloadSerializer("blocklist");
        BlockListPayload blockList = new BlockListPayload();
        blockList.addItem(new JID("romeo@montague.net"));
        blockList.addItem(new JID("iago@shakespeare.lit"));
        String serializedBlockList = testling.serialize(blockList);
        String expected = "<blocklist xmlns=\"urn:xmpp:blocking\">"
                            + "<item jid=\"romeo@montague.net\"/>"
                            + "<item jid=\"iago@shakespeare.lit\"/>"
                        + "</blocklist>";
        assertEquals(expected,serializedBlockList);
    }
    
    @Test
    public void testExample6() {
        BlockBlockPayloadSerializer testling = new BlockBlockPayloadSerializer("block");
        BlockPayload block = new BlockPayload();
        block.addItem(new JID("romeo@montague.net"));
        String serializedBlock = testling.serialize(block);
        String expected = "<block xmlns=\"urn:xmpp:blocking\">"
                            + "<item jid=\"romeo@montague.net\"/>"
                        + "</block>";
        assertEquals(expected,serializedBlock);
    }
    
    @Test
    public void testExample10() {
        BlockUnblockPayloadSerializer testling = new BlockUnblockPayloadSerializer("unblock");
        UnblockPayload unblock = new UnblockPayload();
        unblock.addItem(new JID("romeo@montague.net"));
        String serializedBlock = testling.serialize(unblock);
        String expected = "<unblock xmlns=\"urn:xmpp:blocking\">"
                            + "<item jid=\"romeo@montague.net\"/>"
                        + "</unblock>";
        assertEquals(serializedBlock,expected);
    }

}
