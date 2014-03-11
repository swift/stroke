/*
* Copyright (c) 2014 Kevin Smith and Remko Tronçon
* All rights reserved.
*/

/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/

package com.isode.stroke.serializer.payloadserializers;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import com.isode.stroke.elements.ResultSet;

public class ResultSetSerializerTest {

    @Test
    public void testSerializeFull() {
        ResultSetSerializer serializer = new ResultSetSerializer();
    
        ResultSet resultSet = new ResultSet();
    
        resultSet.setMaxItems(new Long(100));
        resultSet.setCount(new Long(800));
        resultSet.setFirstIDIndex(new Long(123));
        resultSet.setFirstID("stpeter@jabber.org");
        resultSet.setLastID("peterpan@neverland.lit");
        resultSet.setAfter("09af3-cc343-b409f");
        resultSet.setBefore("decaf-badba-dbad1");
    
        String expectedResult = 
            "<set xmlns=\"http://jabber.org/protocol/rsm\">"
          +     "<max>100</max>"
          +     "<count>800</count>"
          +     "<first index=\"123\">stpeter@jabber.org</first>"
          +     "<last>peterpan@neverland.lit</last>"
          +     "<after>09af3-cc343-b409f</after>"
          +     "<before>decaf-badba-dbad1</before>"
          + "</set>";
    
        assertEquals(expectedResult, serializer.serialize(resultSet));
    }

    @Test
    public void testSerializeMaxItems() {
        ResultSetSerializer serializer = new ResultSetSerializer();
    
        ResultSet resultSet = new ResultSet();
    
        resultSet.setMaxItems(new Long(100));
    
        String expectedResult = 
            "<set xmlns=\"http://jabber.org/protocol/rsm\">"
          +     "<max>100</max>"
          + "</set>";
    
        assertEquals(expectedResult, serializer.serialize(resultSet));
    }

    @Test
    public void testSerializeEmptyBefore() {
        ResultSetSerializer serializer = new ResultSetSerializer();
    
        ResultSet resultSet = new ResultSet();
    
        resultSet.setBefore(new String());
    
        String expectedResult = 
            "<set xmlns=\"http://jabber.org/protocol/rsm\">"
          +     "<before/>"
          + "</set>";
    
        assertEquals(expectedResult, serializer.serialize(resultSet));
    }

    @Test
    public void testSerializeFirst() {
        ResultSetSerializer serializer = new ResultSetSerializer();
    
        ResultSet resultSet = new ResultSet();
    
        resultSet.setFirstID("stpeter@jabber.org");
    
        String expectedResult = 
            "<set xmlns=\"http://jabber.org/protocol/rsm\">"
          +     "<first>stpeter@jabber.org</first>"
          + "</set>";
    
        assertEquals(expectedResult, serializer.serialize(resultSet));
    }

    @Test
    public void testSerializeFirstWithIndex() {
        ResultSetSerializer serializer = new ResultSetSerializer();
    
        ResultSet resultSet = new ResultSet();
    
        resultSet.setFirstID("stpeter@jabber.org");
        resultSet.setFirstIDIndex(new Long(123));
    
        String expectedResult = 
            "<set xmlns=\"http://jabber.org/protocol/rsm\">"
          +     "<first index=\"123\">stpeter@jabber.org</first>"
          + "</set>";
    
        assertEquals(expectedResult, serializer.serialize(resultSet));
    }
}
