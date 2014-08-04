/*
 * Copyright (c) 2010 Remko Tronçon
 * All rights reserved.
 */
/*
 * Copyright (c) 2011-2014, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.tls;

import com.isode.stroke.base.Error;

public class CertificateVerificationError implements Error {

    public enum Type {

        UnknownError,
        Expired,
        NotYetValid,
        SelfSigned,
        Rejected,
        Untrusted,
        InvalidPurpose,
        PathLengthExceeded,
        InvalidSignature,
        InvalidCA,
        InvalidServerIdentity,
        Revoked,
        RevocationCheckFailed
    }

    public CertificateVerificationError(Type type) {
        if (type == null) {
            throw new IllegalStateException();
        }
        this.type = type;
    }
    public final Type type;
}

