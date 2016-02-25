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

import com.isode.stroke.base.DateTime;
import com.isode.stroke.elements.Idle;

/**
 * Test for {@link IdleSerializer}
 *
 */
public class IdleSerializerTest {

    @Test
    public void testSerialize() {
        IdleSerializer testling = new IdleSerializer();
        Idle idle = new Idle(DateTime.stringToDate("1969-07-21T02:56:15Z"));

        assertEquals("<idle xmlns='urn:xmpp:idle:1' since='1969-07-21T02:56:15Z'/>",
                testling.serialize(idle));
    }

}
