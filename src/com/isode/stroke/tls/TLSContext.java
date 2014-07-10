/*
 * Copyright (c) 2011-2014, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.tls;

import java.util.List;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.signals.Signal;
import com.isode.stroke.signals.Signal1;

public abstract class TLSContext {

    public abstract void connect();

    public abstract boolean setClientCertificate(CertificateWithKey cert);

    public abstract void handleDataFromNetwork(ByteArray data);
    public abstract void handleDataFromApplication(ByteArray data);

    /**
     * The peer certificate, as presented by the remote entity
     * @return the peer certificate, which may be null
     */
    public abstract Certificate getPeerCertificate();
    /**
     * The peer's certificate chain, as presented by the remote entity
     * @return the peer certificate chain, which may be null.
     */
    public abstract List<Certificate> getPeerCertificateChain();
    public abstract CertificateVerificationError getPeerCertificateVerificationError();

    public abstract ByteArray getFinishMessage();

    public Signal1<ByteArray> onDataForNetwork = new Signal1<ByteArray>();
    public Signal1<ByteArray> onDataForApplication = new Signal1<ByteArray>();
    public Signal onError = new Signal();
    public Signal onConnected = new Signal();
}
