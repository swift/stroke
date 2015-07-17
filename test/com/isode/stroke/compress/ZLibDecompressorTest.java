/*
 * Copyright (c) 2010 Remko Tronçon
 * Licensed under the GNU General Public License v3.
 * See Documentation/Licenses/GPLv3.txt for more information.
 */
/*
 * Copyright (c) 2010-2011, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.compress;

import com.isode.stroke.base.SafeByteArray;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Kev
 */
public class ZLibDecompressorTest {

    @Test
    public void testProcess() throws ZLibException {
        ZLibDecompressor testling = new ZLibDecompressor();
        SafeByteArray result = testling.process(ZLibCompressorTest.unhex("78da4acbcf07000000ffff"));

        assertEquals(new SafeByteArray("foo"), result);
    }

    @Test
    public void testProcess_Twice() throws ZLibException {
        ZLibDecompressor testling = new ZLibDecompressor();
        testling.process(ZLibCompressorTest.unhex("78da4acbcf07000000ffff"));
        SafeByteArray result = testling.process(ZLibCompressorTest.unhex("4a4a2c02000000ffff"));

        assertEquals(new SafeByteArray("bar"), result);
    }

    @Test(expected = ZLibException.class)
    public void testProcess_Invalid() throws ZLibException {
        ZLibDecompressor testling = new ZLibDecompressor();
        testling.process(new SafeByteArray("invalid"));
    }

    @Test
    public void testProcess_Huge() throws ZLibException {
        SafeByteArray data = new SafeByteArray();
        for (int i = 0; i < 2048; ++i) {
            data.append((byte) i);
        }
        SafeByteArray original = new SafeByteArray(data);
        SafeByteArray compressed = new ZLibCompressor().process(original);
        SafeByteArray decompressed = new ZLibDecompressor().process(compressed);

        assertEquals(original, decompressed);
    }

    @Test
    public void testProcess_ChunkSize() throws ZLibException {
        SafeByteArray data = new SafeByteArray();
        for (int i = 0; i < 1024; ++i) {
            data.append((byte) i);
        }
        SafeByteArray original = new SafeByteArray(data);
        SafeByteArray compressed = new ZLibCompressor().process(original);
        SafeByteArray decompressed = new ZLibDecompressor().process(compressed);

        assertEquals(original, decompressed);
    }
}
