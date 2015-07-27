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

package com.isode.stroke.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Before;
import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.network.HostAddress;

public class HostAddressTest {

	@Test
	public void testConstructor() {
		HostAddress testling = new HostAddress("192.168.1.254");

		assertEquals(("192.168.1.254"), testling.toString());
		assertTrue(testling.isValid());
	}

	@Test
	public void testConstructor_Invalid() {
		HostAddress testling = new HostAddress();

		assertFalse(testling.isValid());
	}

	@Test
	public void testConstructor_InvalidString() {
		HostAddress testling = new HostAddress("invalid");

		assertFalse(testling.isValid());
	}

	@Test
	public void testToString() {
		char address[] = {10, 0, 1, 253}; 
		HostAddress testling = new HostAddress(address, 4);

		assertEquals(("10.0.1.253"), testling.toString());
	}

	@Test
	public void testToString_IPv6() {
		char address[] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 17}; 
		HostAddress testling = new HostAddress(address, 16);

		assertEquals(("102:304:506:708:90a:b0c:d0e:f11"), testling.toString());
	}

	@Test
	public void testToString_Invalid() {
		HostAddress testling = new HostAddress();

		assertEquals("<no address>", testling.toString());
	}
}

