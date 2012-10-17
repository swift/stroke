/*
 * Copyright (c) 2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.stringcodecs;

import com.isode.stroke.base.ByteArray;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class HexifyTest {

    private ByteArray fix(int[] vals) {
        byte[] fixed = new byte[vals.length];
        for (int i = 0; i < fixed.length; i++) {
            fixed[i] = (byte) vals[i];
        }
        return new ByteArray(fixed);
    }

    @Test
    public void testHexify() {
        assertEquals("4206b23ca6b0a643d20d89b04ff58cf78b8096ed", Hexify.hexify(fix(new int[]{0x42, 0x06, 0xb2, 0x3c, 0xa6, 0xb0, 0xa6, 0x43, 0xd2, 0x0d, 0x89, 0xb0, 0x4f, 0xf5, 0x8c, 0xf7, 0x8b, 0x80, 0x96, 0xed})));
    }

    @Test
    public void testHexify_Byte() {
        assertEquals("b2", Hexify.hexify((byte) 0xb2));
    }

    @Test
    public void testUnhexify() {
        assertEquals("ffaf02", Hexify.hexify(Hexify.unhexify("ffaf02")));
        assertEquals(new ByteArray(fix(new int[]{0x01, 0x23, 0xf2})), Hexify.unhexify("0123f2"));
    }
};

