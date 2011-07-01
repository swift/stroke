/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.serializer;

import com.isode.stroke.elements.Payload;
import java.util.Vector;

public class PayloadSerializerCollection {

    private final Vector<PayloadSerializer> serializers_ = new Vector<PayloadSerializer>();

    public void addSerializer(PayloadSerializer serializer) {
        synchronized (serializers_) {
            serializers_.add(serializer);
        }
    }

    public PayloadSerializer getPayloadSerializer(Payload payload) {
        synchronized (serializers_) {
            for (PayloadSerializer serializer : serializers_) {
                if (serializer.canSerialize(payload)) {
                    return serializer;
                }
            }
        }
        return null;
    }
}
