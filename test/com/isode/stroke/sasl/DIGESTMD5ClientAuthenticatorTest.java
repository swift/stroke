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

package com.isode.stroke.sasl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Before;
import com.isode.stroke.sasl.DIGESTMD5ClientAuthenticator;
import com.isode.stroke.base.ByteArray;
import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.crypto.JavaCryptoProvider;

public class DIGESTMD5ClientAuthenticatorTest {

	private CryptoProvider crypto;

	@Before
	public void setUp() {
		crypto = new JavaCryptoProvider();
	}

	@Test
	public void testGetInitialResponse() {
		DIGESTMD5ClientAuthenticator testling = new DIGESTMD5ClientAuthenticator("xmpp.example.com", "abcdefgh", crypto);

		assertNull(testling.getResponse());
	}

	@Test
	public void testGetResponse() {
		DIGESTMD5ClientAuthenticator testling = new DIGESTMD5ClientAuthenticator("xmpp.example.com", "abcdefgh", crypto);

		testling.setCredentials("user", new SafeByteArray("pass"), "");
		testling.setChallenge(new ByteArray(
			"realm=\"example.com\","
		+	"nonce=\"O6skKPuaCZEny3hteI19qXMBXSadoWs840MchORo\","
		+	"qop=auth,charset=utf-8,algorithm=md5-sess"));

		SafeByteArray response = testling.getResponse();

		assertEquals(new SafeByteArray("charset=utf-8,cnonce=\"abcdefgh\",digest-uri=\"xmpp/xmpp.example.com\",nc=00000001,nonce=\"O6skKPuaCZEny3hteI19qXMBXSadoWs840MchORo\",qop=auth,realm=\"example.com\",response=088891c800ecff1b842159ad6459104a,username=\"user\""), response);
	}

	@Test
	public void testGetResponse_WithAuthorizationID() {
		DIGESTMD5ClientAuthenticator testling = new DIGESTMD5ClientAuthenticator("xmpp.example.com", "abcdefgh", crypto);

		testling.setCredentials("user", new SafeByteArray("pass"), "myauthzid");
		testling.setChallenge(new ByteArray(
			"realm=\"example.com\","
		+	"nonce=\"O6skKPuaCZEny3hteI19qXMBXSadoWs840MchORo\","
		+	"qop=auth,charset=utf-8,algorithm=md5-sess"));

		SafeByteArray response = testling.getResponse();

		assertEquals(new SafeByteArray("authzid=\"myauthzid\",charset=utf-8,cnonce=\"abcdefgh\",digest-uri=\"xmpp/xmpp.example.com\",nc=00000001,nonce=\"O6skKPuaCZEny3hteI19qXMBXSadoWs840MchORo\",qop=auth,realm=\"example.com\",response=4293834432b6e7889a2dee7e8fe7dd06,username=\"user\""), response);
	}
}
