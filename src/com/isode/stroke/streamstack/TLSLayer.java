/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.streamstack;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.signals.Signal;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.tls.Certificate;
import com.isode.stroke.tls.CertificateVerificationError;
import com.isode.stroke.tls.PKCS12Certificate;
import com.isode.stroke.tls.TLSContext;
import com.isode.stroke.tls.TLSContextFactory;

public class TLSLayer extends StreamLayer {

    public TLSLayer(TLSContextFactory factory) {
        context = factory.createTLSContext();
        context.onDataForNetwork.connect(new Slot1<ByteArray>() {

            public void call(ByteArray p1) {
                writeDataToChildLayer(p1);
            }
        });
        context.onDataForApplication.connect(new Slot1<ByteArray>() {

            public void call(ByteArray p1) {
                writeDataToParentLayer(p1);
            }
        });
        context.onConnected.connect(onConnected);
        context.onError.connect(onError);
    }

    public void connect() {
        context.connect();
    }

    public void writeData(ByteArray data) {
        context.handleDataFromApplication(data);
    }

    public void handleDataRead(ByteArray data) {
        context.handleDataFromNetwork(data);
    }

    public boolean setClientCertificate(PKCS12Certificate certificate) {
        return context.setClientCertificate(certificate);
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

    public final Signal onError = new Signal();
    public final Signal onConnected = new Signal();

    private final TLSContext context;
}
