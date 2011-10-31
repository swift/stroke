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
import com.isode.stroke.stringcodecs.Hexify;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Kev
 */
public class ZLibCompressorTest {

    @Test
    public void testProcess() throws Exception {
        ZLibCompressor testling = new ZLibCompressor();
        ByteArray result = testling.process(new ByteArray("foo"));

        assertEquals("78da4acbcf07000000ffff", Hexify.hexify(result));
    }

    @Test
    public void testProcess_Twice() throws ZLibException {
        ZLibCompressor testling = new ZLibCompressor();
        testling.process(new ByteArray("foo"));
        ByteArray result = testling.process(new ByteArray("bar"));

        assertEquals("4a4a2c02000000ffff", Hexify.hexify(result));
    }

    public static ByteArray unhex(String string) {
        HexBinaryAdapter adaptor = new HexBinaryAdapter();
        return new ByteArray(adaptor.unmarshal(string));
    }
}
