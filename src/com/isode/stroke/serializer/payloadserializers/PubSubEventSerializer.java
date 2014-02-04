/*
 * Copyright (c) 2014, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2014, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.serializer.payloadserializers;

import java.util.ArrayList;

import com.isode.stroke.elements.PubSubEventPayload;
import com.isode.stroke.parser.payloadparsers.PubSubEvent;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.PayloadSerializer;
import com.isode.stroke.serializer.PayloadSerializerCollection;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.serializer.xml.XMLRawTextNode;

public class PubSubEventSerializer extends GenericPayloadSerializer<PubSubEvent> {
    public PubSubEventSerializer(PayloadSerializerCollection serializer) {
        super(PubSubEvent.class);
    }
    
    protected String serializePayload(PubSubEvent payload) {
        if (payload == null) {
            return "";
        }
        XMLElement element = new XMLElement("event", "http://jabber.org/protocol/pubsub#event");
        PubSubEventPayload p = payload.getPayload();
        
        for (PayloadSerializer serializer : pubsubSerializers_) {
            if (serializer.canSerialize(p)) {
                element.addNode(new XMLRawTextNode(serializer.serialize(p)));
            }
        }
        return element.serialize();
    }
    
    PayloadSerializerCollection serializers_;
    ArrayList<PayloadSerializer> pubsubSerializers_ = new ArrayList<PayloadSerializer>();
}
