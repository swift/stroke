/*
 * Copyright (c) 2010 Remko Tronçon
 * Licensed under the GNU General Public License v3.
 * See Documentation/Licenses/GPLv3.txt for more information.
 */
/*
 * Copyright (c) 2011, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.streamstack;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.compress.ZLibCompressor;
import com.isode.stroke.compress.ZLibDecompressor;
import com.isode.stroke.compress.ZLibException;
import com.isode.stroke.signals.Signal;

public class CompressionLayer extends StreamLayer {

    public void writeData(ByteArray data) {
        try {
            writeDataToChildLayer(compressor_.process(data));
        }
        catch (ZLibException e) {
            onError.emit();
        }
    }

    public void handleDataRead(ByteArray data) {
        try {
            writeDataToParentLayer(decompressor_.process(data));
        }
        catch (ZLibException e) {
            onError.emit();
        }
    }

    public Signal onError = new Signal();

    private ZLibCompressor compressor_ = new ZLibCompressor();
    private ZLibDecompressor decompressor_ = new ZLibDecompressor();

}
