/*
 * Copyright (c) 2011, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.compress;

import com.isode.stroke.base.ByteArray;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 *
 * @author Kev
 */
public class ZLibDecompressor {
    Inflater inflater_ = new Inflater();
    public ByteArray process(ByteArray data) throws ZLibException {
        try {
            inflater_.setInput(data.getData());
            byte[] output = new byte[100];
            ByteArray result = new ByteArray();
            int size = 0;
            while ((size = inflater_.inflate(output)) != 0) {
                for (int i = 0; i < size; i++) {
                    result.append(output[i]); /* TODO: Terribly slow */
                }
            }
            return result;
        }
        catch (DataFormatException e) {
            throw new ZLibException();
        }
    }

}
