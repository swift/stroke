/*  Copyright (c) 2016, Isode Limited, London, England.
 *  All rights reserved.
 *
 *  Acquisition and use of this software and related materials for any
 *  purpose requires a written license agreement from Isode Limited,
 *  or a written license from an organisation licensed by Isode Limited
 *  to grant such a license.
 *
 */
package com.isode.stroke.client;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import com.isode.stroke.jid.JID;
import com.isode.stroke.signals.Slot1;

public class BlockListImplTest {
    
    private final BlockListImpl blockList_ = new BlockListImpl();
    private final List<JID> addedJIDs_ = new ArrayList<JID>();
    private final List<JID> removedJIDs_ = new ArrayList<JID>();
    
    @Before
    public void setUp() {
        blockList_.addItem(new JID("a@example.com"));
        blockList_.addItem(new JID("b@example.com"));
        
        blockList_.onItemAdded.connect(new Slot1<JID>() {
            
            @Override
            public void call(JID jid) {
                handleBlockListItemAdded(jid);
            }
            
        });
        
        blockList_.onItemRemoved.connect(new Slot1<JID>() {

            @Override
            public void call(JID jid) {
                handleBlockListItemRemoved(jid);
            }
            
        });
    }
    
    @Test
    public void testSetItemsToSubset() {
        Vector<JID> subset = new Vector<JID>();
        subset.add(new JID("a@example.com"));

        blockList_.setItems(subset);

        assertEquals(0, addedJIDs_.size());
        assertEquals(1, removedJIDs_.size());
    }
    
    @Test
    public void testSetItemsToSuperset() {
        Vector<JID> superset = new Vector<JID>();
        superset.add(new JID("a@example.com"));
        superset.add(new JID("b@example.com"));
        superset.add(new JID("c@example.com"));

        blockList_.setItems(superset);

        assertEquals(1, addedJIDs_.size());
        assertEquals(0, removedJIDs_.size());
    }
    
    @Test
    public void testSetItemsAllDifferent() {
        Vector<JID> newBlockList = new Vector<JID>();
        newBlockList.add(new JID("x@example.com"));
        newBlockList.add(new JID("y@example.com"));
        newBlockList.add(new JID("z@example.com"));

        blockList_.setItems(newBlockList);

        assertEquals(3, addedJIDs_.size());
        assertEquals(2, removedJIDs_.size());
    }
    
    private void handleBlockListItemAdded(JID jid) {
        addedJIDs_.add(jid);
    }

    private void handleBlockListItemRemoved(JID jid) {
        removedJIDs_.add(jid);
    }

}
