/*  Copyright (c) 2016, Isode Limited, London, England.
 *  All rights reserved.
 *
 *  Acquisition and use of this software and related materials for any
 *  purpose requires a written license agreement from Isode Limited,
 *  or a written license from an organisation licensed by Isode Limited
 *  to grant such a license.
 *
 */
package com.isode.stroke.elements;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.isode.stroke.elements.ErrorPayload.Condition;
import com.isode.stroke.elements.ErrorPayload.Type;
import com.isode.stroke.jid.JID;

/**
 * Unit test for {@link IQ}
 */
public class IQTest {

    @Test
    public void testCreateResult() {
        Payload payload = new SoftwareVersion("myclient");
        IQ iq = IQ.createResult(new JID("foo@bar/fum"), "myid", payload);
        
        assertEquals(new JID("foo@bar/fum"),iq.getTo());
        assertEquals("myid",iq.getID());
        assertEquals(payload,iq.getPayload(new SoftwareVersion()));
    }
    
    @Test
    public void testCreateResult_WithoutPayload() {
        IQ iq = IQ.createError(new JID("foo@bar/fum"), "myid");
        
        assertEquals(new JID("foo@bar/fum"),iq.getTo());
        assertEquals("myid",iq.getID());
        assertNull(iq.getPayload(new SoftwareVersion()));
    }
    
    @Test
    public void testCreateError() {
        IQ iq = IQ.createError(new JID("foo@bar/fum"), "myid", Condition.BadRequest, Type.Modify);
        
        assertEquals(new JID("foo@bar/fum"),iq.getTo());
        assertEquals("myid",iq.getID());
        ErrorPayload error = iq.getPayload(new ErrorPayload());
        assertEquals(Condition.BadRequest,error.getCondition());
        assertEquals(Type.Modify,error.getType());
    }

}
