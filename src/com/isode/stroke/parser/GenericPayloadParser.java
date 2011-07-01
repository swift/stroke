/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.parser;

import com.isode.stroke.elements.Payload;

public abstract class GenericPayloadParser <T extends Payload> implements PayloadParser {
    private T payload_;

    public GenericPayloadParser(T payload) {
        payload_ = payload;
    }

    public Payload getPayload() {
        return payload_;
    }

    protected T getPayloadInternal() {
        return payload_;
    }
}
