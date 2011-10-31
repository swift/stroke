/*
 * Copyright (c) 2011 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010 Remko Tronçon.
 * All rights reserved.
 */

package com.isode.stroke.tls;

import com.isode.stroke.base.ByteArray;

public interface CertificateFactory {
    Certificate createCertificateFromDER(ByteArray der);
}
