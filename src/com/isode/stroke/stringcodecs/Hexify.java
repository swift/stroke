/*
 * Copyright (c) 2011-2012 Isode Limited, London, England.
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
        return String.format("%02x", new Byte(datum));
    }

    public static String hexify(ByteArray data) {
        StringBuilder result = new StringBuilder();
        for (byte b : data.getData()) {
            result.append(hexify(b));
        }
        return result.toString();
    }



    public static ByteArray unhexify(String hexstring) {
        if (hexstring.length() % 2 != 0) {
                return new ByteArray();
        }
        byte[] result = new byte[hexstring.length() / 2];
        for (int pos = 0; pos < hexstring.length() - 1; pos += 2) {
                char c;
                c = hexstring.charAt(pos);
                int a = (c>='0'&&c<='9') ? c-'0' : (c>='A'&&c<='Z') ? c-'A' + 10 : (c>='a'&&c<='z') ? c-'a' + 10 : -1;
                c = hexstring.charAt(pos+1);
                int b = (c>='0'&&c<='9') ? c-'0' : (c>='A'&&c<='Z') ? c-'A' + 10 : (c>='a'&&c<='z') ? c-'a' + 10 : -1;
                if (a == -1 || b == -1) return new ByteArray(); // fail
                result[pos/2] = (byte) ((a << 4) | b);

        }
        return new ByteArray(result);
    }
}
