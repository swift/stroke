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

/**
 * Serialise a particular payload.
 */
public abstract class PayloadSerializer {
    public abstract boolean canSerialize(Payload payload);
    public abstract String serialize(Payload payload);
}
