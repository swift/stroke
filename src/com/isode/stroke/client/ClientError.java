/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.client;

/**
 *
 */
public class ClientError {

    private final Type type_;

    enum Type {

        UnknownError,
        DomainNameResolveError,
        ConnectionError,
        ConnectionReadError,
        ConnectionWriteError,
        XMLError,
        AuthenticationFailedError,
        CompressionFailedError,
        ServerVerificationFailedError,
        NoSupportedAuthMechanismsError,
        UnexpectedElementError,
        ResourceBindError,
        SessionStartError,
        TLSError,
        ClientCertificateLoadError,
        ClientCertificateError,
        // Certificate verification errors
        UnknownCertificateError,
        CertificateExpiredError,
        CertificateNotYetValidError,
        CertificateSelfSignedError,
        CertificateRejectedError,
        CertificateUntrustedError,
        InvalidCertificatePurposeError,
        CertificatePathLengthExceededError,
        InvalidCertificateSignatureError,
        InvalidCAError,
        InvalidServerIdentityError,
    };

    ClientError(Type type) {
        type_ = type;
    }

    public Type getType() {
        return type_;
    }
}
