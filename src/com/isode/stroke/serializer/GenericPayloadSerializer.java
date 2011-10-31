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

public abstract class GenericPayloadSerializer<T extends Payload> extends PayloadSerializer {

    private final Class class_;

    public GenericPayloadSerializer(Class c) {
        class_ = c;
    }

    @Override
    public boolean canSerialize(Payload payload) {
        return class_.isAssignableFrom(payload.getClass());
    }

    @Override
    public String serialize(Payload payload) {
        return serializePayload((T)payload);
    }

    protected abstract String serializePayload(T payload);

}
