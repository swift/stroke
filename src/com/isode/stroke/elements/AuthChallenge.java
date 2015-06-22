/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.elements;

import com.isode.stroke.base.ByteArray;

public class AuthChallenge implements Element {
    //FIXME: parser/serialiser
    public AuthChallenge() {

    }

    public AuthChallenge(ByteArray value) {
        value_ = new ByteArray(value);
    }

    public ByteArray getValue() {
        return value_;
    }

    public void setValue(ByteArray value) {
        value_ = new ByteArray(value);
    }

    private ByteArray value_;
}
