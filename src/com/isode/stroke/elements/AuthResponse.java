/*
 * Copyright (c) 2010 Remko Tronçon
 * Licensed under the GNU General Public License v3.
 * See Documentation/Licenses/GPLv3.txt for more information.
 */
/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.elements;

import com.isode.stroke.base.ByteArray;

public class AuthResponse implements Element {
    //FIXME: parser/serialiser

    public AuthResponse() {
        value = null;
    }

    public AuthResponse(ByteArray value) {
        this.value = value;
    }

    public ByteArray getValue() {
        return value;
    }

    public void setValue(ByteArray value) {
        this.value = value;
    }
    private ByteArray value;
}
