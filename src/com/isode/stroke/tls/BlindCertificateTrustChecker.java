/*
 * Copyright (c) 2010-2015 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.tls;

import com.isode.stroke.tls.CertificateTrustChecker;
import java.util.List;

/**
 * A certificate trust checker that trusts any ceritficate.
 *
 * This can be used to ignore any TLS certificate errors occurring 
 * during connection.
 *
 * @link Client#setAlwaysTrustCertificates()
 */
public class BlindCertificateTrustChecker implements CertificateTrustChecker {

	public boolean isCertificateTrusted(final List<Certificate> certificate) {
		return true;
	}
}