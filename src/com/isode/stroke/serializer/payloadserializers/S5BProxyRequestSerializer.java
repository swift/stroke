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

import com.isode.stroke.elements.S5BProxyRequest;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.xml.XMLElement;

/**
 * 
 */
public class S5BProxyRequestSerializer extends
        GenericPayloadSerializer<S5BProxyRequest> {

    /**
     * Constructor
     */
    public S5BProxyRequestSerializer() {
        super(S5BProxyRequest.class);
    }

    @Override
    protected String serializePayload(S5BProxyRequest s5bProxyRequest) {
        XMLElement queryElement = new XMLElement("query", "http://jabber.org/protocol/bytestreams");
        if (s5bProxyRequest != null && s5bProxyRequest.getStreamHost() != null) {
            XMLElement streamHost = new XMLElement("streamhost");
            streamHost.setAttribute("host", s5bProxyRequest.getStreamHost().host);
            streamHost.setAttribute("port", String.valueOf(s5bProxyRequest.getStreamHost().port));
            streamHost.setAttribute("jid", s5bProxyRequest.getStreamHost().jid.toString());
            queryElement.addNode(streamHost);
        }
        else if (s5bProxyRequest != null && s5bProxyRequest.getActivate() != null) {
            queryElement.setAttribute("sid", s5bProxyRequest.getSID());
            XMLElement active = new XMLElement("active","",s5bProxyRequest.getActivate().toString());
            queryElement.addNode(active);
        }
        return queryElement.serialize();
    }

}
