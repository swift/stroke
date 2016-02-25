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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.isode.stroke.base.DateTime;
import com.isode.stroke.elements.Idle;
import com.isode.stroke.elements.Presence;
import com.isode.stroke.parser.PresenceParser;
import com.isode.stroke.parser.StanzaParserTester;

/**
 * Test for {@link IdleParser}
 */
public class IdleParserTest {

    @Test
    public void testParse_XepWhatever_Example1() {
        PresenceParser testling = new PresenceParser(new FullPayloadParserFactoryCollection());
        StanzaParserTester<PresenceParser> parser = new StanzaParserTester<PresenceParser>(testling);
        assertTrue(parser.parse(
            "<presence from='juliet@capulet.com/balcony'>\n"
                +"<show>away</show>\n"
                +"<idle xmlns='urn:xmpp:idle:1' since='1969-07-21T02:56:15Z'/>\n"
            +"</presence>\n"
        ));

        Presence presence = testling.getStanzaGeneric();
        assertNotNull(presence);
        
        Idle idle = presence.getPayload(new Idle());
        assertNotNull(idle);
        assertEquals(DateTime.stringToDate("1969-07-21T02:56:15Z"),idle.getSince());
    }

}
