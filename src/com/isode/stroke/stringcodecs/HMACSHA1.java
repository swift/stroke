/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.stringcodecs;

import com.isode.stroke.base.ByteArray;

public class HMACSHA1 {

    private static final int B = 64;

    public static ByteArray getResult(ByteArray key, ByteArray data) {
        assert key.getSize() <= B;

        /* And an assert that does something */
        if (key.getSize() > B) {
            throw new IllegalStateException("Invalid key size.");
        }

        // Create the padded key
        ByteArray paddedKey = new ByteArray(key);
        for (int i = key.getSize(); i < B; ++i) {
            paddedKey.append((byte) 0x0);
        }

        // Create the first value
        ByteArray x = new ByteArray(paddedKey);
        byte[] xInner = x.getData();
        for (int i = 0; i < xInner.length; ++i) {
            xInner[i] ^= 0x36;
        }
        x.append(data);

        // Create the second value
        ByteArray y = new ByteArray(paddedKey);
        byte[] yInner = y.getData();
        for (int i = 0; i < yInner.length; ++i) {
            yInner[i] ^= 0x5c;
        }
        y.append(SHA1.getHash(x));

        return SHA1.getHash(y);
    }
}
