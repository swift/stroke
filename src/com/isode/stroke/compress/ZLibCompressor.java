/*
 * Copyright (c) 2011, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.compress;

import com.isode.stroke.base.ByteArray;
import java.util.zip.Deflater;

/**
 *
 * @author Kev
 */
public class ZLibCompressor {

    private static final int COMPRESSION_LEVEL = 9;

    public ByteArray process(ByteArray data) throws ZLibException {
        Deflater compressor = new Deflater(COMPRESSION_LEVEL);
        compressor.setStrategy(Deflater.DEFAULT_STRATEGY);
        compressor.setInput(data.getData());
        compressor.finish();
        byte[] output = new byte[100];
        ByteArray result = new ByteArray();
        while (!compressor.finished()) {
            int size = compressor.deflate(output);
            for (int i = 0; i < size; i++) {
                result.append(output[i]); /* TODO: Terribly slow */
            }
        }
        return result;
    }
}
