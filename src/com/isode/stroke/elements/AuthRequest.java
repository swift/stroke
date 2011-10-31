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

public class AuthRequest implements Element {

    public AuthRequest() {
        this(null);
    }

    //FIXME: parser/serialiser
    public AuthRequest(String mechanism) {
        mechanism_ = mechanism;
    }

    public AuthRequest(String mechanism, ByteArray message) {
        mechanism_ = mechanism;
        message_ = message;
    }

    public ByteArray getMessage() {
        return message_;
    }

    public void setMessage(ByteArray message) {
        message_ = message;
    }

    public String getMechanism() {
        return mechanism_;
    }

    public void setMechanism(String mechanism) {
        mechanism_ = mechanism;
    }
    private String mechanism_;
    private ByteArray message_ = new ByteArray();
}
