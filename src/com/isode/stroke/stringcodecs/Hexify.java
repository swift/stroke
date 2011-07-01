/*
 * Copyright (c) 2011 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.stringcodecs;

import com.isode.stroke.base.ByteArray;

public class Hexify {

    public static String hexify(byte datum) {
        return String.format("%x", new Byte(datum));
    }

    public static String hexify(ByteArray data) {
        StringBuilder result = new StringBuilder();
        for (byte b : data.getData()) {
            result.append(hexify(b));
        }
        return result.toString();
    }
}
