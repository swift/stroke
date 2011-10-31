/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.sasl;

import com.isode.stroke.base.ByteArray;

public class PLAINClientAuthenticator extends ClientAuthenticator {
    public PLAINClientAuthenticator() {
        super("PLAIN");
    }

    public ByteArray getResponse() {
        return new ByteArray().append(getAuthorizationID()).append((byte)0).append(getAuthenticationID()).append((byte)0).append(getPassword());
    }

    public boolean setChallenge(ByteArray challenge) {
        return true;
    }
}

