/*
 * Copyright (c) 2013-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.crypto;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.base.SafeByteArray;

public abstract class CryptoProvider {

    public abstract Hash createSHA1();
    public abstract Hash createMD5();
    public abstract ByteArray getHMACSHA1(final SafeByteArray key, final ByteArray data);
    public abstract ByteArray getHMACSHA1(final ByteArray key, final ByteArray data);
    public abstract boolean isMD5AllowedForCrypto();

    // Convenience
    public ByteArray getSHA1Hash(final SafeByteArray data) {
        return createSHA1().update(data).getHash();
    }

    public ByteArray getSHA1Hash(final ByteArray data) {
        return createSHA1().update(data).getHash();
    }
    
    public ByteArray getMD5Hash(final SafeByteArray data) {
        return createMD5().update(data).getHash();
    }

    public ByteArray getMD5Hash(final ByteArray data) {
        return createMD5().update(data).getHash();
    }

}
