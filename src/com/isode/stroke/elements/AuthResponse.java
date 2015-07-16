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

import com.isode.stroke.base.SafeByteArray;

public class AuthResponse implements Element {
    //FIXME: parser/serialiser

    public AuthResponse() {
        value = null;
    }

    public AuthResponse(SafeByteArray value) {
        this.value = value;
    }

    public SafeByteArray getValue() {
        return value;
    }

    public void setValue(SafeByteArray value) {
        this.value = value;
    }
    private SafeByteArray value;
}
