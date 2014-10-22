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
import com.isode.stroke.elements.Form;
import com.isode.stroke.elements.FormField;
import com.isode.stroke.elements.FormField.Type;
import com.isode.stroke.elements.MAMQuery;
import com.isode.stroke.elements.ResultSet;
import static org.junit.Assert.assertEquals;

public class MAMQuerySerializerTest {

    @Test
    public void testSerialize() {
        MAMQuerySerializer serializer = new MAMQuerySerializer();

        Form parameters = new Form();
        
        FormField fieldType = new FormField(Type.TEXT_SINGLE_TYPE, "urn:xmpp:mam:0");
        fieldType.setName("FORM_TYPE");
        parameters.addField(fieldType);

        FormField fieldStart = new FormField(Type.TEXT_SINGLE_TYPE, "2010-08-07T00:00:00Z");
        fieldStart.setName("start");
        parameters.addField(fieldStart);

        ResultSet set = new ResultSet();
        set.setMaxItems(new Long(10));

        MAMQuery query = new MAMQuery();
        query.setQueryID("id0");
        query.setForm(parameters);
        query.setResultSet(set);

        String expectedResult =
            "<query queryid=\"id0\" xmlns=\"urn:xmpp:mam:0\">"
          +     "<x type=\"form\" xmlns=\"jabber:x:data\">"
          +         "<field type=\"text-single\" var=\"FORM_TYPE\">"
          +            "<value>urn:xmpp:mam:0</value>"
          +         "</field>"
          +         "<field type=\"text-single\" var=\"start\">"
          +             "<value>2010-08-07T00:00:00Z</value>"
          +         "</field>"
          +     "</x>"
          +     "<set xmlns=\"http://jabber.org/protocol/rsm\">"
          +         "<max>10</max>"
          +     "</set>"
          + "</query>";

        assertEquals(expectedResult, serializer.serialize(query));
    }
}
