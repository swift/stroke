/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */

package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.Payload;
import com.isode.stroke.elements.PrivateStorage;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.PayloadSerializer;
import com.isode.stroke.serializer.PayloadSerializerCollection;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.serializer.xml.XMLRawTextNode;

class PrivateStorageSerializer extends GenericPayloadSerializer<PrivateStorage>{
    
    private final PayloadSerializerCollection serializers;

    public PrivateStorageSerializer(PayloadSerializerCollection serializers) {
        super(PrivateStorage.class);
        this.serializers = serializers;
    }


    @Override
    protected String serializePayload(PrivateStorage storage) {
        XMLElement storageElement = new XMLElement("query", "jabber:iq:private");
        Payload payload = storage.getPayload();
        if (payload != null) {
            PayloadSerializer serializer = serializers.getPayloadSerializer(payload);
            if (serializer != null) {
                storageElement.addNode(new XMLRawTextNode(serializer.serialize(payload)));
            }
        }
        return storageElement.serialize();
    }

}
