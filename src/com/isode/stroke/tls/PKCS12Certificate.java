/*
 * Copyright (c) 2011 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.tls;

import com.isode.stroke.base.ByteArray;

public class PKCS12Certificate {

    public PKCS12Certificate() {
    }

    public PKCS12Certificate(String filename, String password) {
        password_ = password;
        data_.readFromFile(filename);
    }

    public boolean isNull() {
        return data_.isEmpty();
    }

    public ByteArray getData() {
        return data_;
    }

    public void setData(ByteArray data) {
        data_ = data;
    }

    public String getPassword() {
        return password_;
    }
    private ByteArray data_;
    private String password_;
}
