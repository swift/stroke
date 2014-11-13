package com.isode.stroke.parser.payloadparsers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.isode.stroke.elements.MAMFin;
import com.isode.stroke.eventloop.DummyEventLoop;

public class MAMFinParserTest {

    // From swiften test 'testParse_XEP0313_Exmaple1'
    @Test
    public void testParse_XEP0313_Example1() {
        DummyEventLoop eventLoop = new DummyEventLoop();
        PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
        assertTrue(parser.parse("<fin xmlns='urn:xmpp:mam:0' queryid='f27' />"));
        
        MAMFin payload = (MAMFin)parser.getPayload();
        assertTrue(payload != null);
        assertEquals(false,payload.isComplete());
        assertEquals(true,payload.isStable());
        
        String queryID = payload.getQueryID();
        assertTrue(queryID != null);
        assertEquals("f27",queryID);
    }
    
    // From swiften test 'testParse_XEP0313_Exmaple9'
    @Test
    public void testParse_XEP0313_Example9() {
        DummyEventLoop eventLoop = new DummyEventLoop();
        PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
        assertTrue(parser.parse(
            "<fin xmlns='urn:xmpp:mam:0' complete='true'>"
                +"<set xmlns='http://jabber.org/protocol/rsm'>"
                    +"<first index='0'>23452-4534-1</first>"
                    +"<last>390-2342-22</last>"
                    +"<count>16</count>"
                +"</set>"
            +"</fin>"));
        
        MAMFin payload = (MAMFin)parser.getPayload();
        assertTrue(payload != null);
        assertEquals(true,payload.isComplete());
        assertEquals(true,payload.isStable());
        
        assertTrue(payload.getResultSet() != null);
    }

}
