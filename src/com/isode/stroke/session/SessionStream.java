/*
 * Copyright (c) 2010 Remko Tronçon
 * Licensed under the GNU General Public License v3.
 * See Documentation/Licenses/GPLv3.txt for more information.
 */
/*
 * Copyright (c) 2010-2011, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.session;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.elements.Element;
import com.isode.stroke.elements.ProtocolHeader;
import com.isode.stroke.signals.Signal;
import com.isode.stroke.signals.Signal1;
import com.isode.stroke.tls.Certificate;
import com.isode.stroke.tls.CertificateVerificationError;
import com.isode.stroke.tls.PKCS12Certificate;

public abstract class SessionStream {

    public static class Error implements com.isode.stroke.base.Error {

        public enum Type {

            ParseError,
            TLSError,
            InvalidTLSCertificateError,
            ConnectionReadError,
            ConnectionWriteError
        };

        public Error(Type type) {
            this.type = type;
        }
        public final Type type;
    };

    public abstract void close();

    public abstract boolean isOpen();

    public abstract void writeHeader(ProtocolHeader header);

    public abstract void writeFooter();

    public abstract void writeElement(Element element);

    public abstract void writeData(String data);

    public abstract void addZLibCompression();

    public abstract boolean supportsTLSEncryption();

    public abstract void addTLSEncryption();

    public abstract boolean isTLSEncrypted();

    public abstract void setWhitespacePingEnabled(boolean enabled);

    public abstract void resetXMPPParser();
    
    public void setTLSCertificate(PKCS12Certificate cert) {
        certificate = cert;
    }

    public boolean hasTLSCertificate() {
        return certificate != null && !certificate.isNull();
    }

    public abstract Certificate getPeerCertificate();

    public abstract CertificateVerificationError getPeerCertificateVerificationError();

    public abstract ByteArray getTLSFinishMessage();

    public final Signal1<ProtocolHeader> onStreamStartReceived = new Signal1<ProtocolHeader>();
    public final Signal1<Element> onElementReceived = new Signal1<Element>();
    public final Signal1<Error> onClosed = new Signal1<Error>();
    public final Signal onTLSEncrypted = new Signal();
    public final Signal1<String> onDataRead = new Signal1<String>();
    public final Signal1<String> onDataWritten = new Signal1<String>();
    protected PKCS12Certificate getTLSCertificate() {
        return certificate;
    }
    private PKCS12Certificate certificate;
}
