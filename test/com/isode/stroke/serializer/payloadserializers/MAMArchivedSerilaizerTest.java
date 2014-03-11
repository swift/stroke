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
import com.isode.stroke.elements.MAMArchived;
import com.isode.stroke.jid.JID;
import static org.junit.Assert.assertEquals;

public class MAMArchivedSerilaizerTest {

    @Test
    public void testSerialize() {
        MAMArchivedSerializer serializer = new MAMArchivedSerializer();

        MAMArchived archived = new MAMArchived();
        archived.setBy(JID.fromString("juliet@capulet.lit"));
        archived.setID("28482-98726-73623");

        String expectedResult =
            "<archived by=\"juliet@capulet.lit\" id=\"28482-98726-73623\" xmlns=\"urn:xmpp:mam:0\"/>";

        assertEquals(expectedResult, serializer.serialize(archived));
    }
}
