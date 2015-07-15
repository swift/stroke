/*
 * Copyright (c) 2010-2014 Remko Tronçon
 * All rights reserved.
 */
/*
 * Copyright (c) 2010-2014, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.session;

import java.util.List;

import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.base.ByteArray;
import com.isode.stroke.elements.Element;
import com.isode.stroke.elements.ProtocolHeader;
import com.isode.stroke.signals.Signal;
import com.isode.stroke.signals.Signal1;
import com.isode.stroke.tls.Certificate;
import com.isode.stroke.tls.CertificateVerificationError;
import com.isode.stroke.tls.CertificateWithKey;

public abstract class SessionStream {

    public static class Error implements com.isode.stroke.base.Error {

        public enum Type {

            ParseError,
            TLSError,
            InvalidTLSCertificateError,
            ConnectionReadError,
            ConnectionWriteError
        }

        public Error(Type type) {
            this.type = type;
        }
        public final Type type;
    }

    public abstract void close();

    public abstract boolean isOpen();

    public abstract void writeHeader(ProtocolHeader header);

    public abstract void writeFooter();

    public abstract void writeElement(Element element);

    public abstract void writeData(String data);

    public abstract boolean supportsZLibCompression();

    public abstract void addZLibCompression();

    public abstract boolean supportsTLSEncryption();

    public abstract void addTLSEncryption();

    public abstract boolean isTLSEncrypted();

    public abstract void setWhitespacePingEnabled(boolean enabled);

    public abstract void resetXMPPParser();
    
    public void setTLSCertificate(CertificateWithKey cert) {
        certificate = cert;
    }

    public boolean hasTLSCertificate() {
        return certificate != null && !certificate.isNull();
    }

    public abstract List<Certificate> getPeerCertificateChain();
    public abstract Certificate getPeerCertificate();

    public abstract CertificateVerificationError getPeerCertificateVerificationError();

    public abstract ByteArray getTLSFinishMessage();

    public final Signal1<ProtocolHeader> onStreamStartReceived = new Signal1<ProtocolHeader>();
    public final Signal1<Element> onElementReceived = new Signal1<Element>();
    public final Signal1<Error> onClosed = new Signal1<Error>();
    public final Signal onTLSEncrypted = new Signal();
    public final Signal1<SafeByteArray> onDataRead = new Signal1<SafeByteArray>();
    public final Signal1<SafeByteArray> onDataWritten = new Signal1<SafeByteArray>();
    protected CertificateWithKey getTLSCertificate() {
        return certificate;
    }
    
    @Override
    public String toString() {
        String className = this.getClass().getSimpleName();

        // Include actual type based on class name of the object
        return className + 
        "; supportsTLSEncryption:" + supportsTLSEncryption() +
        "; " + (hasTLSCertificate() ? "has" : "no") +
        " certificate";            
    }
    private CertificateWithKey certificate;
}
