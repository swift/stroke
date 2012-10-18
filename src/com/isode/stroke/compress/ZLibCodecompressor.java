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

import com.isode.stroke.base.ByteArray;
import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZStream;

public abstract class ZLibCodecompressor {
    protected static final int CHUNK_SIZE = 1024;
    protected final ZStream stream_ = new ZStream();

    public ByteArray process(ByteArray input) throws ZLibException {
        ByteArray output = new ByteArray();
	stream_.avail_in = input.getSize();
	stream_.next_in = input.getData();
        stream_.next_in_index = 0;
        do {
            byte[] outputArray = new byte[CHUNK_SIZE];
            stream_.avail_out = CHUNK_SIZE;
            stream_.next_out = outputArray;
            stream_.next_out_index = 0;
            int result = processZStream();
            if (result != JZlib.Z_OK && result != JZlib.Z_BUF_ERROR) {
                throw new ZLibException(/* stream_.msg */);
            }
            output.append(outputArray, CHUNK_SIZE - stream_.avail_out);
	}
	while (stream_.avail_out == 0);
	if (stream_.avail_in != 0) {
		throw new ZLibException();
	}
	return output;
    }

    protected abstract int processZStream();
}
