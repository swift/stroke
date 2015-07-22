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

package com.isode.stroke.sasl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Before;
import com.isode.stroke.sasl.PLAINClientAuthenticator;
import com.isode.stroke.base.SafeByteArray;

public class PLAINClientAuthenticatorTest {

	@Test
	public void testGetResponse_WithoutAuthzID() {
		PLAINClientAuthenticator testling = new PLAINClientAuthenticator();

		testling.setCredentials("user", new SafeByteArray("pass"));

		assertEquals(testling.getResponse(), new SafeByteArray("\0user\0pass"));
	}

	@Test
	public void testGetResponse_WithAuthzID() {
		PLAINClientAuthenticator testling = new PLAINClientAuthenticator();

		testling.setCredentials("user", new SafeByteArray("pass"), "authz");

		assertEquals(testling.getResponse(), new SafeByteArray("authz\0user\0pass"));
	}	
}