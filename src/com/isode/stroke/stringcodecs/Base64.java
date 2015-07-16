/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.stringcodecs;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.base.SafeByteArray;

public class Base64 {
    /* FIXME: Check license is ok (it is, it's BSD) */
    public static ByteArray decode(String input) {
        return new ByteArray(Base64BSD.decode(input));
    }

    public static String encode(ByteArray input) {
        return Base64BSD.encodeToString(input.getData(), false);
    }

    public static SafeByteArray encode(SafeByteArray input) {
        return new SafeByteArray(Base64BSD.encodeToString(input.getData(), false));
    }

    public static String encode(byte[] input) {
        return Base64BSD.encodeToString(input, false);
    }
}
