/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2011, Kevin Smith
 * All rights reserved.
 */
package com.isode.stroke.serializer.payloadserializers;

import static org.junit.Assert.*;

import org.junit.Test;

import com.isode.stroke.elements.MUCAdminPayload;
import com.isode.stroke.elements.MUCItem;
import com.isode.stroke.elements.MUCOccupant;
import com.isode.stroke.jid.JID;
import com.isode.stroke.serializer.payloadserializers.MUCAdminPayloadSerializer;

public class MUCAdminPayloadSerializerTest {
    
    @Test
    public void testSerialize() {
        MUCAdminPayloadSerializer testling = new MUCAdminPayloadSerializer();
        MUCAdminPayload admin = new MUCAdminPayload();
        MUCItem item = new MUCItem();
        item.affiliation = MUCOccupant.Affiliation.Owner;
        item.role = MUCOccupant.Role.Visitor;
        item.reason = "malice";
        item.actor = new JID("kev@tester.lit");
        admin.addItem(item);

        assertEquals("<query xmlns=\"http://jabber.org/protocol/muc#admin\"><item affiliation=\"owner\" " +
                "role=\"visitor\"><actor jid=\"kev@tester.lit\"/><reason>malice</reason></item></query>", 
                testling.serialize(admin));
    }

}
