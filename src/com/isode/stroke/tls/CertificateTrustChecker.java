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

/**
 * A class to implement a check for certificate trust.
 */
public interface CertificateTrustChecker {

    /**
     * This method is called to find out whether a certificate is
     * trusted. This usually happens when a certificate's validation
     * fails, to check whether to proceed with the connection or not.
     */
    boolean isCertificateTrusted(Certificate certificate);
}
