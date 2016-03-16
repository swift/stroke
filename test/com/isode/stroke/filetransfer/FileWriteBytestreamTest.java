/*  Copyright (c) 2016, Isode Limited, London, England.
 *  All rights reserved.
 *
 *  Acquisition and use of this software and related materials for any
 *  purpose requires a written license agreement from Isode Limited,
 *  or a written license from an organisation licensed by Isode Limited
 *  to grant such a license.
 *
 */
package com.isode.stroke.filetransfer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.signals.Slot1;

/**
 * Tests for {@link FileWriteBytestream}
 *
 */
public class FileWriteBytestreamTest {

    private boolean onWriteWasCalled = false;
    
    @Test
    public void testSuccessfulWrite() {
        File tempfile = null;
        String filename = null;
        try {
            try {
                tempfile = File.createTempFile("write_file_bytestream_test_", ".tmp");
                filename = tempfile.getAbsolutePath();
            } catch (IOException e) {
                // Unable to create file exit test
                return;
            }
            WriteBytestream writeBytestream = new FileWriteBytestream(filename);
            writeBytestream.onWrite.connect(new Slot1<ByteArray>() {
                
                @Override
                public void call(ByteArray data) {
                    handleOnWrite(data);
                }
                
            });
            
            assertTrue(writeBytestream.write(new ByteArray("Some data.")));
            assertTrue(onWriteWasCalled);
        }
        finally {
            if (tempfile != null && tempfile.exists()) {
                tempfile.delete();
            }
        }
    }
    
    @Test
    public void testFailingWrite() {
        WriteBytestream writeBytestream = new FileWriteBytestream("");
        writeBytestream.onWrite.connect(new Slot1<ByteArray>() {
            
            @Override
            public void call(ByteArray data) {
                handleOnWrite(data);
            }
            
        });
        
        assertFalse(writeBytestream.write(new ByteArray("Some data.")));
        assertFalse(onWriteWasCalled);
    }
    
    private void handleOnWrite(ByteArray data) {
        onWriteWasCalled = true;
    }
    
}
