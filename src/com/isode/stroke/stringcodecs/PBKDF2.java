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

public class PBKDF2 {

    public static ByteArray encode(ByteArray password, ByteArray salt, int iterations) {
        ByteArray u = HMACSHA1.getResult(password, ByteArray.plus(salt, new ByteArray("\0\0\0\1")));
        ByteArray result = new ByteArray(u);
        byte[] resultData = result.getData();
        int i = 1;
        while (i < iterations) {
            u = HMACSHA1.getResult(password, u);
            for (int j = 0; j < u.getSize(); ++j) {
                resultData[j] ^= u.getData()[j];
            }
            ++i;
        }
        result = new ByteArray(resultData);
        return result;
    }
}
