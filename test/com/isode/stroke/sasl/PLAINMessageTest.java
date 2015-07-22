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
import com.isode.stroke.sasl.PLAINMessage;
import com.isode.stroke.base.SafeByteArray;

public class PLAINMessageTest {

	@Test
	public void testGetValue_WithoutAuthzID() {
		PLAINMessage message = new PLAINMessage("user", new SafeByteArray("pass"));
		assertEquals(message.getValue(), new SafeByteArray("\0user\0pass"));
	}

	@Test
	public void testGetValue_WithAuthzID() {
		PLAINMessage message = new PLAINMessage("user", new SafeByteArray("pass"), "authz");
		assertEquals(message.getValue(), new SafeByteArray("authz\0user\0pass"));
	}

	@Test
	public void testConstructor_WithoutAuthzID() {
		PLAINMessage message = new PLAINMessage(new SafeByteArray("\0user\0pass"));

		assertEquals((""), message.getAuthorizationID());
		assertEquals(("user"), message.getAuthenticationID());
		assertEquals(new SafeByteArray("pass"), message.getPassword());
	}

	@Test
	public void testConstructor_WithAuthzID() {
		PLAINMessage message = new PLAINMessage(new SafeByteArray("authz\0user\0pass"));

		assertEquals(("authz"), message.getAuthorizationID());
		assertEquals(("user"), message.getAuthenticationID());
		assertEquals(new SafeByteArray("pass"), message.getPassword());
	}

	@Test
	public void testConstructor_NoAuthcid() {
		PLAINMessage message = new PLAINMessage(new SafeByteArray("authzid"));

		assertEquals((""), message.getAuthenticationID());
	}

	@Test
	public void testConstructor_NoPassword() {
		PLAINMessage message = new PLAINMessage(new SafeByteArray("authzid\0authcid"));

		assertEquals((""), message.getAuthenticationID());
	}
}
