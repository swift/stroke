/*
 * Copyright (c) 2010 Isode Limited.
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
import com.isode.stroke.tls.ServerIdentityVerifier;
import com.isode.stroke.base.ByteArray;
import com.isode.stroke.idn.IDNConverter;
import com.isode.stroke.idn.ICUConverter;
import com.isode.stroke.jid.JID;

public class ServerIdentityVerifierTest {

	private IDNConverter idnConverter;

	@Before
	public void setUp() {
		idnConverter = new ICUConverter();
	}

	@Test
	public void testCertificateVerifies_WithoutMatchingDNSName() {
		ServerIdentityVerifier testling = new ServerIdentityVerifier(new JID("foo@bar.com/baz"), idnConverter);
		SimpleCertificate certificate = new SimpleCertificate();
		certificate.addDNSName("foo.com");

		assertFalse(testling.certificateVerifies(certificate));
	}

	@Test
	public void testCertificateVerifies_WithMatchingDNSName() {
		ServerIdentityVerifier testling = new ServerIdentityVerifier(new JID("foo@bar.com/baz"), idnConverter);
		SimpleCertificate certificate = new SimpleCertificate();
		certificate.addDNSName("bar.com");

		assertTrue(testling.certificateVerifies(certificate));
	}

	@Test
	public void testCertificateVerifies_WithSecondMatchingDNSName() {
		ServerIdentityVerifier testling = new ServerIdentityVerifier(new JID("foo@bar.com/baz"), idnConverter);
		SimpleCertificate certificate = new SimpleCertificate();
		certificate.addDNSName("foo.com");
		certificate.addDNSName("bar.com");

		assertTrue(testling.certificateVerifies(certificate));
	}

	@Test
	public void testCertificateVerifies_WithMatchingInternationalDNSName() {
		ServerIdentityVerifier testling = new ServerIdentityVerifier(new JID("foo@tronçon.com/baz"), idnConverter);
		SimpleCertificate certificate = new SimpleCertificate();
		certificate.addDNSName("xn--tronon-zua.com");

		assertTrue(testling.certificateVerifies(certificate));
	}

	@Test
	public void testCertificateVerifies_WithMatchingDNSNameWithWildcard() {
		ServerIdentityVerifier testling = new ServerIdentityVerifier(new JID("foo@im.bar.com/baz"), idnConverter);
		SimpleCertificate certificate = new SimpleCertificate();
		certificate.addDNSName("*.bar.com");

		assertTrue(testling.certificateVerifies(certificate));
	}

	@Test
	public void testCertificateVerifies_WithMatchingDNSNameWithWildcardMatchingNoComponents() {
		ServerIdentityVerifier testling = new ServerIdentityVerifier(new JID("foo@bar.com/baz"), idnConverter);
		SimpleCertificate certificate = new SimpleCertificate();
		certificate.addDNSName("*.bar.com");

		assertFalse(testling.certificateVerifies(certificate));
	}

	@Test
	public void testCertificateVerifies_WithDNSNameWithWildcardMatchingTwoComponents() {
		ServerIdentityVerifier testling = new ServerIdentityVerifier(new JID("foo@xmpp.im.bar.com/baz"), idnConverter);
		SimpleCertificate certificate = new SimpleCertificate();
		certificate.addDNSName("*.bar.com");

		assertFalse(testling.certificateVerifies(certificate));
	}

	@Test
	public void testCertificateVerifies_WithMatchingSRVNameWithoutService() {
		ServerIdentityVerifier testling = new ServerIdentityVerifier(new JID("foo@bar.com/baz"), idnConverter);
		SimpleCertificate certificate = new SimpleCertificate();
		certificate.addSRVName("bar.com");

		assertFalse(testling.certificateVerifies(certificate));
	}

	@Test
	public void testCertificateVerifies_WithMatchingSRVNameWithService() {
		ServerIdentityVerifier testling = new ServerIdentityVerifier(new JID("foo@bar.com/baz"), idnConverter);
		SimpleCertificate certificate = new SimpleCertificate();
		certificate.addSRVName("_xmpp-client.bar.com");

		assertTrue(testling.certificateVerifies(certificate));
	}

	@Test
	public void testCertificateVerifies_WithMatchingSRVNameWithServiceAndWildcard() {
		ServerIdentityVerifier testling = new ServerIdentityVerifier(new JID("foo@im.bar.com/baz"), idnConverter);
		SimpleCertificate certificate = new SimpleCertificate();
		certificate.addSRVName("_xmpp-client.*.bar.com");

		assertTrue(testling.certificateVerifies(certificate));
	}

	@Test
	public void testCertificateVerifies_WithMatchingSRVNameWithDifferentService() {
		ServerIdentityVerifier testling = new ServerIdentityVerifier(new JID("foo@bar.com/baz"), idnConverter);
		SimpleCertificate certificate = new SimpleCertificate();
		certificate.addSRVName("_xmpp-server.bar.com");

		assertFalse(testling.certificateVerifies(certificate));
	}

	@Test
	public void testCertificateVerifies_WithMatchingXmppAddr() {
		ServerIdentityVerifier testling = new ServerIdentityVerifier(new JID("foo@bar.com/baz"), idnConverter);
		SimpleCertificate certificate = new SimpleCertificate();
		certificate.addXMPPAddress("bar.com");

		assertTrue(testling.certificateVerifies(certificate));
	}

	@Test
	public void testCertificateVerifies_WithMatchingXmppAddrWithWildcard() {
		ServerIdentityVerifier testling = new ServerIdentityVerifier(new JID("foo@im.bar.com/baz"), idnConverter);
		SimpleCertificate certificate = new SimpleCertificate();
		certificate.addXMPPAddress("*.bar.com");

		assertFalse(testling.certificateVerifies(certificate));
	}

	@Test
	public void testCertificateVerifies_WithMatchingInternationalXmppAddr() {
		ServerIdentityVerifier testling = new ServerIdentityVerifier(new JID("foo@tronçon.com/baz"), idnConverter);
		SimpleCertificate certificate = new SimpleCertificate();
		certificate.addXMPPAddress("tronçon.com");

		assertTrue(testling.certificateVerifies(certificate));
	}

	@Test
	public void testCertificateVerifies_WithMatchingCNWithoutSAN() {
		ServerIdentityVerifier testling = new ServerIdentityVerifier(new JID("foo@bar.com/baz"), idnConverter);
		SimpleCertificate certificate = new SimpleCertificate();
		certificate.addCommonName("bar.com");

		assertTrue(testling.certificateVerifies(certificate));
	}

	@Test
	public void testCertificateVerifies_WithMatchingCNWithSAN() {
		ServerIdentityVerifier testling = new ServerIdentityVerifier(new JID("foo@bar.com/baz"), idnConverter);
		SimpleCertificate certificate = new SimpleCertificate();
		certificate.addSRVName("foo.com");
		certificate.addCommonName("bar.com");

		assertFalse(testling.certificateVerifies(certificate));
	}
}