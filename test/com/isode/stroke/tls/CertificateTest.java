/*
 * Copyright (c) 2010-2013 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.tls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Before;
import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.crypto.JavaCryptoProvider;
import com.isode.stroke.tls.SimpleCertificate;
import com.isode.stroke.tls.Certificate;
import com.isode.stroke.base.ByteArray;

public class CertificateTest {

	@Test
	public void testGetSHA1Fingerprint() {
		SimpleCertificate testling = new SimpleCertificate();
		testling.setDER(new ByteArray("abcdefg"));

		assertEquals("2f:b5:e1:34:19:fc:89:24:68:65:e7:a3:24:f4:76:ec:62:4e:87:40", Certificate.getSHA1Fingerprint(testling, new JavaCryptoProvider()));
	}
}
