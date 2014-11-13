package com.isode.stroke.serializer.payloadserializers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.isode.stroke.elements.MAMFin;
import com.isode.stroke.elements.ResultSet;

public class MAMFinSerializerTest {

    // From swiften test testSerialize_XEP0313_Exmaple1
    @Test
    public void testSerialize_XEP0313_Example1() {
        MAMFinSerializer serializer = new MAMFinSerializer();
        
        MAMFin fin = new MAMFin();
        fin.setQueryID("f27");
        
        String expectedResult = "<fin queryid=\"f27\" xmlns=\"urn:xmpp:mam:0\"/>";
        assertEquals(expectedResult,serializer.serialize(fin));
    }

    // From swiften test testSerialize_XEP0313_Exmaple9
    @Test
    public void testSerialize_XEP0313_Example9() {
        MAMFinSerializer serializer = new MAMFinSerializer();
        
        MAMFin fin = new MAMFin();
        fin.setComplete(true);
        
        ResultSet resultSet = new ResultSet();
        resultSet.setFirstID("23452-4534-1");
        resultSet.setFirstIDIndex(Long.valueOf(0));
        resultSet.setLastID("390-2342-22");
        resultSet.setCount(Long.valueOf(16));
        
        fin.setResultSet(resultSet);
        String expectedResult =
                "<fin complete=\"true\" xmlns=\"urn:xmpp:mam:0\">"
                     +"<set xmlns=\"http://jabber.org/protocol/rsm\">"
                         +"<count>16</count>"
                         +"<first index=\"0\">23452-4534-1</first>"
                         +"<last>390-2342-22</last>"
                     +"</set>"
                 +"</fin>";
        assertEquals(expectedResult, serializer.serialize(fin));
    }
    
    
}
