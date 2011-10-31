/*
 * Copyright (c) 2011, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.elements;

public class CompressRequest implements Element {

    public CompressRequest() {
        this("");
    }

    public CompressRequest(String method) {
        method_ = method;
    }

    public String getMethod() {
        return method_;
    }

    public void setMethod(String method) {
        method_ = method;
    }
    private String method_;
}
