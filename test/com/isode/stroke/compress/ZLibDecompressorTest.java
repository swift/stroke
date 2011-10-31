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

import com.isode.stroke.base.ByteArray;
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
        ByteArray result = testling.process(ZLibCompressorTest.unhex("78da4acbcf07000000ffff"));

        assertEquals(new ByteArray("foo"), result);
    }

    @Test
    public void testProcess_Twice() throws ZLibException {
        ZLibDecompressor testling = new ZLibDecompressor();
        testling.process(ZLibCompressorTest.unhex("78da4acbcf07000000ffff"));
        ByteArray result = testling.process(ZLibCompressorTest.unhex("4a4a2c02000000ffff"));

        assertEquals(new ByteArray("bar"), result);
    }

    @Test(expected = ZLibException.class)
    public void testProcess_Invalid() throws ZLibException {
        ZLibDecompressor testling = new ZLibDecompressor();
        testling.process(new ByteArray("invalid"));
    }

    @Test
    public void testProcess_Huge() throws ZLibException {
        ByteArray data = new ByteArray();
        for (int i = 0; i < 2048; ++i) {
            data.append((byte) i);
        }
        ByteArray original = new ByteArray(data);
        ByteArray compressed = new ZLibCompressor().process(original);
        ByteArray decompressed = new ZLibDecompressor().process(compressed);

        assertEquals(original, decompressed);
    }

    @Test
    public void testProcess_ChunkSize() throws ZLibException {
        ByteArray data = new ByteArray();
        for (int i = 0; i < 1024; ++i) {
            data.append((byte) i);
        }
        ByteArray original = new ByteArray(data);
        ByteArray compressed = new ZLibCompressor().process(original);
        ByteArray decompressed = new ZLibDecompressor().process(compressed);

        assertEquals(original, decompressed);
    }
}
