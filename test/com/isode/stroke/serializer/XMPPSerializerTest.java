/*  Copyright (c) 2016, Isode Limited, London, England.
 *  All rights reserved.
 *
 *  Acquisition and use of this software and related materials for any
 *  purpose requires a written license agreement from Isode Limited,
 *  or a written license from an organisation licensed by Isode Limited
 *  to grant such a license.
 *
 */
package com.isode.stroke.serializer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.isode.stroke.elements.ProtocolHeader;
import com.isode.stroke.elements.StreamType;
import com.isode.stroke.serializer.payloadserializers.FullPayloadSerializerCollection;

/**
 * 
 */
public class XMPPSerializerTest {

    private final PayloadSerializerCollection payloadSerializerCollection =
            new FullPayloadSerializerCollection();
    
    @Test
    public void testSerializeHeader_Client() {
        XMPPSerializer testling = createSerializer(StreamType.ClientStreamType);
        ProtocolHeader protocolHeader = new ProtocolHeader();
        protocolHeader.setFrom("bla@foo.com");
        protocolHeader.setTo("foo.com");
        protocolHeader.setID("myid");
        protocolHeader.setVersion("0.99");

        assertEquals("<?xml version=\"1.0\"?>"
                + "<stream:stream xmlns=\"jabber:client\" "
                    + "xmlns:stream=\"http://etherx.jabber.org/streams\" "
                    + "from=\"bla@foo.com\" to=\"foo.com\" id=\"myid\" version=\"0.99\">", 
                testling.serializeHeader(protocolHeader));
    }
    
    @Test
    public void testSerializeHeader_Component() {
        XMPPSerializer testling = createSerializer(StreamType.ComponentStreamType);
        ProtocolHeader protocolHeader = new ProtocolHeader();
        protocolHeader.setFrom("bla@foo.com");
        protocolHeader.setTo("foo.com");
        protocolHeader.setID("myid");
        protocolHeader.setVersion("0.99");
        
        assertEquals("<?xml version=\"1.0\"?>"
                + "<stream:stream xmlns=\"jabber:component:accept\" "
                    + "xmlns:stream=\"http://etherx.jabber.org/streams\" "
                    + "from=\"bla@foo.com\" to=\"foo.com\" id=\"myid\" version=\"0.99\">",
                testling.serializeHeader(protocolHeader));
    }
    
    @Test
    public void testSerializeHeader_Server() {
        XMPPSerializer testling = createSerializer(StreamType.ServerStreamType);
        ProtocolHeader protocolHeader = new ProtocolHeader();
        protocolHeader.setFrom("bla@foo.com");
        protocolHeader.setTo("foo.com");
        protocolHeader.setID("myid");
        protocolHeader.setVersion("0.99");
        
        assertEquals("<?xml version=\"1.0\"?>"
                + "<stream:stream xmlns=\"jabber:server\" "
                    + "xmlns:stream=\"http://etherx.jabber.org/streams\" "
                    + "from=\"bla@foo.com\" to=\"foo.com\" id=\"myid\" version=\"0.99\">", 
                testling.serializeHeader(protocolHeader));
    }
    
    private XMPPSerializer createSerializer(StreamType type) {
        return new XMPPSerializer(payloadSerializerCollection, type, false);
    }
    
}
