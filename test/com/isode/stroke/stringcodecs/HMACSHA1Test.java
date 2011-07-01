/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tron?on.
 * All rights reserved.
 */

package com.isode.stroke.stringcodecs;

import com.isode.stroke.base.ByteArray;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class HMACSHA1Test {

    private ByteArray cast(int[] source) {
        byte[] result = new byte[source.length];
        for (int i = 0; i < source.length; i++) {
            result[i] = (byte)source[i];
        }
        return new ByteArray(result);
    }

    @Test
    public void testGetResult() {
	ByteArray result = HMACSHA1.getResult(new ByteArray("foo"), new ByteArray("foobar"));
	assertEquals(cast(new int[]{0xa4, 0xee, 0xba, 0x8e, 0x63, 0x3d, 0x77, 0x88, 0x69, 0xf5, 0x68, 0xd0, 0x5a, 0x1b, 0x3d, 0xc7, 0x2b, 0xfd, 0x4, 0xdd}), result);
    }
}
