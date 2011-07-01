/*
 * Copyright (c) 2010-2011, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.base;

import java.io.UnsupportedEncodingException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ByteArrayTest {
    @Test
    public void testASCIIString_fromString() {
        String testling = "Wheeblahpling";
        assertEquals(testling, new ByteArray(testling).toString());
    }

    @Test
    public void testASCIIString_fromBytesSimple() throws UnsupportedEncodingException {
        String testling = "WheeBlahBlahPling";
        byte[] bytes = testling.getBytes("UTF-8");
        assertEquals(testling, new ByteArray(bytes).toString());
    }

    @Test
    public void testASCIIString_fromBytes() throws UnsupportedEncodingException {
        String target = "ABCZ";
        byte[] bytes = {65, 66, 67, 90};
        assertEquals(target, new ByteArray(bytes).toString());
    }

    @Test
    public void testExtendedString_fromString() {
        String testling = "Wheeblahpling\u0041\u00DF\u00F7";
        assertEquals(testling, new ByteArray(testling).toString());
    }

    @Test
    public void testExtendedString_fromBytes() {
        String target = "ABCZ\u0041\u00DF\u00F7";
        byte[] bytes = byteify(new int[]{65, 66, 67, 90, 0x41, 0xc3, 0x9f, 0xc3, 0xb7});
        assertEquals(target, new ByteArray(bytes).toString());
    }

    @Test
    public void testExtendedString_fromBytesSegmented() {
        String target = "ABCZ\u0041\u00DF\u00F7";
        byte[] bytes = byteify(new int[]{65, 66, 67, 90, 0x41, 0xc3, 0x9f});
        ByteArray testling = new ByteArray(bytes);
        testling.append((byte)0xc3);
        testling.append((byte)0xb7);
        assertEquals(target, testling.toString());
    }

    @Test
    public void testExtendedBytes_fromString() {
        String string = "ABCZ\u0041\u00DF\u00F7";
        byte[] target = byteify(new int[]{65, 66, 67, 90, 0x41, 0xc3, 0x9f, 0xc3, 0xb7});
        assertArrayEquals(target, new ByteArray(string).getData());
    }

    private byte[] byteify(int[] ints) {
        byte[] bytes = new byte[ints.length];
        for (int i = 0; i < ints.length; i++) {
            int j = ints[i];
            bytes[i] = (byte)j;
        }
        return bytes;
    }
}
