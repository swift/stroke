/*
* Copyright (c) 2014 Kevin Smith and Remko Tronçon
* All rights reserved.
*/

/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/

package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.Forwarded;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.elements.Message;
import com.isode.stroke.elements.Presence;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.IQSerializer;
import com.isode.stroke.serializer.MessageSerializer;
import com.isode.stroke.serializer.PayloadSerializerCollection;
import com.isode.stroke.serializer.PresenceSerializer;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.serializer.xml.XMLRawTextNode;

public class ForwardedSerializer extends GenericPayloadSerializer<Forwarded> {
    public ForwardedSerializer(PayloadSerializerCollection serializers) {
        super(Forwarded.class);
        serializers_ = serializers;
    }
    
    public String serializePayload(Forwarded payload) {
        if (payload == null) {
            return "";
        }
    
        XMLElement element = new XMLElement("forwarded", "urn:xmpp:forward:0");
    
        if (payload.getDelay() != null) {
            element.addNode(new XMLRawTextNode((new DelaySerializer()).serialize(payload.getDelay())));
        }
    
        if (payload.getStanza() != null) { /* find out what type of stanza we are dealing with and branch into the correct serializer */
            if (payload.getStanza() instanceof IQ) {
                element.addNode(new XMLRawTextNode((new IQSerializer(serializers_)).serialize((IQ)payload.getStanza())));
            } else if (payload.getStanza() instanceof Message) {
                element.addNode(new XMLRawTextNode((new MessageSerializer(serializers_)).serialize((Message)payload.getStanza())));
            } else if (payload.getStanza() instanceof Presence) {
                element.addNode(new XMLRawTextNode((new PresenceSerializer(serializers_)).serialize((Presence)payload.getStanza())));
            }
        }
    
        return element.serialize();
    }

    private PayloadSerializerCollection serializers_;
}
