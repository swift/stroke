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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.isode.stroke.base.ByteArray;

public class FileWriteBytestream extends WriteBytestream {

    private final String filePath_;
    
    private FileOutputStream stream_ = null;
    
    public FileWriteBytestream(String filePath) {
        filePath_ = filePath;
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            close();
        }
        finally {
            super.finalize();
        }
    }
    
    @Override
    public boolean write(ByteArray data) {
        if (data.isEmpty()) {
            return true;
        }
        if (stream_ == null) {
            try {
                stream_ = new FileOutputStream(filePath_);
            } catch (FileNotFoundException e) {
                return false;
            }
        }
        try {
            stream_.write(data.getData());
            stream_.flush();
        } catch (IOException e) {
            return false;
        }
        onWrite.emit(data);
        return true;
    }
    
    public void close() {
        if (stream_ != null) {
            try {
                stream_.close();
            } catch (IOException e) {
                // Ignore exception
            }
            stream_ = null;
        }
    }


}
