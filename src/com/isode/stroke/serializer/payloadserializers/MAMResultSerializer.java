/*
* Copyright (c) 2014 Kevin Smith and Remko Tronçon
* All rights reserved.
*/

/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/

package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.MAMResult;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.PayloadSerializerCollection;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.serializer.xml.XMLRawTextNode;

public class MAMResultSerializer extends GenericPayloadSerializer<MAMResult> {
    public MAMResultSerializer(PayloadSerializerCollection serializers) {
        super(MAMResult.class);
        serializers_ = serializers;
    }

    public String serializePayload(MAMResult payload) {
        if (payload == null) {
            return "";
        }
    
        XMLElement element = new XMLElement("result", "urn:xmpp:mam:0");
    
        element.setAttribute("id", payload.getID());
    
        if (payload.getQueryID() != null) {
            element.setAttribute("queryid", payload.getQueryID());
        }
    
        element.addNode(new XMLRawTextNode((new ForwardedSerializer(serializers_)).serialize(payload.getPayload())));
    
        return element.serialize();
    }
    
    PayloadSerializerCollection serializers_;
}
