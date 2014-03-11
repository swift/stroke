/*
* Copyright (c) 2014 Kevin Smith and Remko Tronçon
* All rights reserved.
*/

/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/

package com.isode.stroke.parser.payloadparsers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import com.isode.stroke.elements.ResultSet;
import com.isode.stroke.eventloop.DummyEventLoop;

public class ResultSetParserTest {

    @Test
    public void testParse() {
        DummyEventLoop eventLoop = new DummyEventLoop();
        PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
        assertTrue(parser.parse(
            "<set xmlns=\"http://jabber.org/protocol/rsm\">"
          +     "<max>100</max>"
          +     "<count>800</count>"
          +     "<first index=\"123\">stpeter@jabber.org</first>"
          +     "<last>peterpan@neverland.lit</last>"
          +     "<after>09af3-cc343-b409f</after>"
          +     "<before>decaf-badba-dbad1</before>"
          + "</set>"));

        assertTrue(parser.getPayload() instanceof ResultSet);
        ResultSet payload = (ResultSet)parser.getPayload();
        assertTrue(payload.getMaxItems() != null);
        assertEquals(new Long(100), payload.getMaxItems());
        assertTrue(payload.getCount() != null);
        assertEquals(new Long(800), payload.getCount());
        assertTrue(payload.getFirstID() != null);
        assertEquals("stpeter@jabber.org", payload.getFirstID());
        assertTrue(payload.getFirstIDIndex() != null);
        assertEquals(new Long(123), payload.getFirstIDIndex());
        assertTrue(payload.getLastID() != null);
        assertEquals("peterpan@neverland.lit", payload.getLastID());
        assertTrue(payload.getAfter() != null);
        assertEquals("09af3-cc343-b409f", payload.getAfter());
        assertTrue(payload.getBefore() != null);
        assertEquals("decaf-badba-dbad1", payload.getBefore());
    }

    @Test
    public void testParseFirstNoIndex() {
        DummyEventLoop eventLoop = new DummyEventLoop();
        PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
        assertTrue(parser.parse(
            "<set xmlns=\"http://jabber.org/protocol/rsm\">"
          +     "<first>stpeter@jabber.org</first>"
          + "</set>"));

        assertTrue(parser.getPayload() instanceof ResultSet);
        ResultSet payload = (ResultSet)parser.getPayload();
        assertTrue(payload.getFirstID() != null);
        assertEquals("stpeter@jabber.org", payload.getFirstID());
        assertTrue(payload.getFirstIDIndex() == null);
    }
}
