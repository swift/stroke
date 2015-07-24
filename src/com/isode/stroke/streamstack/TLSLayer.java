/*
 * Copyright (c) 2010-2014, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.streamstack;

import java.util.List;

import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.signals.Signal;
import com.isode.stroke.signals.Signal1;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.tls.Certificate;
import com.isode.stroke.tls.CertificateVerificationError;
import com.isode.stroke.tls.CertificateWithKey;
import com.isode.stroke.tls.TLSContext;
import com.isode.stroke.tls.TLSOptions;
import com.isode.stroke.tls.TLSError;
import com.isode.stroke.tls.TLSContextFactory;

public class TLSLayer extends StreamLayer {

    public TLSLayer(TLSContextFactory factory, TLSOptions tlsOptions) {
        context = factory.createTLSContext(tlsOptions);
        context.onDataForNetwork.connect(new Slot1<SafeByteArray>() {

            public void call(SafeByteArray p1) {
                writeDataToChildLayer(p1);
            }
        });
        context.onDataForApplication.connect(new Slot1<SafeByteArray>() {

            public void call(SafeByteArray p1) {
                writeDataToParentLayer(p1);
            }
        });
        context.onConnected.connect(onConnected);
        context.onError.connect(onError);
    }

    public void connect() {
        context.connect();
    }

    public void writeData(SafeByteArray data) {
        context.handleDataFromApplication(data);
    }

    public void handleDataRead(SafeByteArray data) {
        context.handleDataFromNetwork(data);
    }

    public boolean setClientCertificate(CertificateWithKey certificate) {
        return context.setClientCertificate(certificate);
    }

    public List<Certificate> getPeerCertificateChain() {
        return context.getPeerCertificateChain();
    }
    
    public Certificate getPeerCertificate() {
        return context.getPeerCertificate();
    }

    public CertificateVerificationError getPeerCertificateVerificationError() {
        return context.getPeerCertificateVerificationError();
    }

    public TLSContext getContext() {
        return context;
    }

    public final Signal1<TLSError> onError = new Signal1<TLSError>();
    public final Signal onConnected = new Signal();

    private final TLSContext context;
}
