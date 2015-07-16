/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.elements;

import com.isode.stroke.base.SafeByteArray;

public class AuthRequest implements Element {

    public AuthRequest() {
        this(null);
    }

    //FIXME: parser/serialiser
    public AuthRequest(String mechanism) {
        mechanism_ = mechanism;
    }

    public AuthRequest(String mechanism, SafeByteArray message) {
        mechanism_ = mechanism;
        message_ = message;
    }

    public SafeByteArray getMessage() {
        return message_;
    }

    public void setMessage(SafeByteArray message) {
        message_ = message;
    }

    public String getMechanism() {
        return mechanism_;
    }

    public void setMechanism(String mechanism) {
        mechanism_ = mechanism;
    }
    private String mechanism_ = "";
    private SafeByteArray message_;
}