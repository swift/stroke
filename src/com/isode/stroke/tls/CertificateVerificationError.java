/*
 * Copyright (c) 2010 Remko Tronçon
 * Licensed under the GNU General Public License v3.
 * See Documentation/Licenses/GPLv3.txt for more information.
 */
/*
 * Copyright (c) 2011, Isode Limited, London, England.
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
    };

    public CertificateVerificationError(Type type) {
        if (type == null) {
            throw new IllegalStateException();
        }
        this.type = type;
    }
    public final Type type;
};

