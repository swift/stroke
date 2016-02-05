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

import com.isode.stroke.signals.Signal1;

/**
 * 
 */
public abstract class SOCKS5AbstractBytestreamSession {

    /**
     * Constructor
     */
    public SOCKS5AbstractBytestreamSession() {
        // Empty Constructor
    }

    abstract public void startReceiving(WriteBytestream writeStream);
    
    abstract public void startSending(ReadBytestream stream);
    
    abstract public void stop();
    
    public final Signal1<FileTransferError> onFinished = new Signal1<FileTransferError>();
    public final Signal1<Integer> onBytesSent = new Signal1<Integer>();
    // boost::signal<void (unsigned long long)> onBytesReceived;
    
}
