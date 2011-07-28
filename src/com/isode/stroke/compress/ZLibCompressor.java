/*
 * Copyright (c) 2010 Remko Tronçon
 * Licensed under the GNU General Public License v3.
 * See Documentation/Licenses/GPLv3.txt for more information.
 */
/*
 * Copyright (c) 2011, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.compress;

import com.jcraft.jzlib.JZlib;

public class ZLibCompressor extends ZLibCodecompressor {

    private static final int COMPRESSION_LEVEL = 9;
    

    public ZLibCompressor() {
        int result = stream_.deflateInit(COMPRESSION_LEVEL);
        assert (result == JZlib.Z_OK);
    }

    protected int processZStream() {
        return stream_.deflate(JZlib.Z_SYNC_FLUSH);
    }
}
