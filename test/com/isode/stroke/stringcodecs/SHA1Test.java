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

public class SHA1Test {

    public SHA1Test() {
    }

    @Test
    public void testGetHash() {
        ByteArray result = new ByteArray(SHA1.getHash(new ByteArray("client/pc//Exodus 0.9.1<http://jabber.org/protocol/caps<http://jabber.org/protocol/disco#info<http://jabber.org/protocol/disco#items<http://jabber.org/protocol/muc<")));
        ByteArray target = new ByteArray(new byte[]{(byte) 0x42, (byte) 0x06, (byte) 0xb2, (byte) 0x3c, (byte) 0xa6, (byte) 0xb0, (byte) 0xa6, (byte) 0x43, (byte) 0xd2, (byte) 0x0d, (byte) 0x89, (byte) 0xb0, (byte) 0x4f, (byte) 0xf5, (byte) 0x8c, (byte) 0xf7, (byte) 0x8b, (byte) 0x80, (byte) 0x96, (byte) 0xed});
        assertEquals(target, result);
    }

    @Test
    public void testGetHash_Twice() {
        ByteArray input = new ByteArray("client/pc//Exodus 0.9.1<http://jabber.org/protocol/caps<http://jabber.org/protocol/disco#info<http://jabber.org/protocol/disco#items<http://jabber.org/protocol/muc<");
        SHA1.getHash(input);
        ByteArray result = SHA1.getHash(input);

        assertEquals(new ByteArray(new byte[]{(byte) 0x42, (byte) 0x06, (byte) 0xb2, (byte) 0x3c, (byte) 0xa6, (byte) 0xb0, (byte) 0xa6, (byte) 0x43, (byte) 0xd2, (byte) 0x0d, (byte) 0x89, (byte) 0xb0, (byte) 0x4f, (byte) 0xf5, (byte) 0x8c, (byte) 0xf7, (byte) 0x8b, (byte) 0x80, (byte) 0x96, (byte) 0xed}), result);
    }

    @Test
    public void testGetHash_NoData() {
        ByteArray result = SHA1.getHash(new ByteArray());

        assertEquals(new ByteArray(new byte[]{(byte) 0xda, (byte) 0x39, (byte) 0xa3, (byte) 0xee, (byte) 0x5e, (byte) 0x6b, (byte) 0x4b, (byte) 0x0d, (byte) 0x32, (byte) 0x55, (byte) 0xbf, (byte) 0xef, (byte) 0x95, (byte) 0x60, (byte) 0x18, (byte) 0x90, (byte) 0xaf, (byte) 0xd8, (byte) 0x07, (byte) 0x09}), result);
    }
}
