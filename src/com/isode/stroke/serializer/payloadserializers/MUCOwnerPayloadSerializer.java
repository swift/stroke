/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2011, Kevin Smith
 * All rights reserved.
 */
package com.isode.stroke.serializer.payloadserializers;

import com.isode.stroke.elements.MUCOwnerPayload;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.serializer.GenericPayloadSerializer;
import com.isode.stroke.serializer.PayloadSerializer;
import com.isode.stroke.serializer.PayloadSerializerCollection;
import com.isode.stroke.serializer.xml.XMLElement;
import com.isode.stroke.serializer.xml.XMLRawTextNode;

/**
 * Serializer for {@link MUCOwnerPayload} element.
 */
public class MUCOwnerPayloadSerializer extends GenericPayloadSerializer<MUCOwnerPayload> {
    public MUCOwnerPayloadSerializer(PayloadSerializerCollection serializers) {
        super(MUCOwnerPayload.class);
        this.serializers_ = serializers;
    }

    @Override
    public String serializePayload(MUCOwnerPayload mucOwner) {
        XMLElement mucElement = new XMLElement("query", "http://jabber.org/protocol/muc#owner");
        Payload payload = mucOwner.getPayload();
        if (payload != null) {
            PayloadSerializer serializer = serializers_.getPayloadSerializer(payload);
            if (serializer != null) {
                mucElement.addNode(new XMLRawTextNode(serializer.serialize(payload)));
            }
        }
        return mucElement.serialize();
    }
    public PayloadSerializerCollection serializers_;
}
