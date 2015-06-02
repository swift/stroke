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

package com.isode.stroke.crypto;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.base.SafeByteArray;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import com.isode.stroke.stringcodecs.Hexify;

public class JavaCryptoProviderTest {

	private CryptoProvider provider;
	
	@Before
	public void setUp() {
		provider = new JavaCryptoProvider();
	}

	////////////////////////////////////////////////////////////
	// SHA-1
	////////////////////////////////////////////////////////////

	@Test
	public void testGetSHA1Hash() {
		Hash shaHash = provider.createSHA1();
		shaHash = shaHash.update(new ByteArray("client/pc//Exodus 0.9.1<http://jabber.org/protocol/caps<http://jabber.org/protocol/disco#info<http://jabber.org/protocol/disco#items<http://jabber.org/protocol/muc<"));
		assertEquals("4206b23ca6b0a643d20d89b04ff58cf78b8096ed", Hexify.hexify(shaHash.getHash()));
	}

	@Test
	public void testGetSHA1Hash_TwoUpdates() {
		Hash shaHash = provider.createSHA1();
		shaHash.update(new ByteArray("client/pc//Exodus 0.9.1<http://jabber.org/protocol/caps<"));
		shaHash.update(new ByteArray("http://jabber.org/protocol/disco#info<http://jabber.org/protocol/disco#items<http://jabber.org/protocol/muc<"));
		assertEquals("4206b23ca6b0a643d20d89b04ff58cf78b8096ed", Hexify.hexify(shaHash.getHash()));
	}

	@Test
	public void testGetSHA1Hash_NoData() {
		Hash shaHash = provider.createSHA1();
		shaHash.update(new ByteArray());
		assertEquals("da39a3ee5e6b4b0d3255bfef95601890afd80709", Hexify.hexify(shaHash.getHash()));
	}

	@Test
	public void testGetSHA1HashStatic() {
		ByteArray returned = provider.getSHA1Hash(new ByteArray("client/pc//Exodus 0.9.1<http://jabber.org/protocol/caps<http://jabber.org/protocol/disco#info<http://jabber.org/protocol/disco#items<http://jabber.org/protocol/muc<"));
		assertEquals("4206b23ca6b0a643d20d89b04ff58cf78b8096ed", Hexify.hexify(returned));
	}

	@Test
	public void testGetSHA1HashStatic_Twice() {
		ByteArray byteArray = new ByteArray("client/pc//Exodus 0.9.1<http://jabber.org/protocol/caps<http://jabber.org/protocol/disco#info<http://jabber.org/protocol/disco#items<http://jabber.org/protocol/muc<");
		ByteArray returned = provider.getSHA1Hash(byteArray);
		assertEquals("4206b23ca6b0a643d20d89b04ff58cf78b8096ed", Hexify.hexify(returned));
	}

	@Test
	public void testGetSHA1HashStatic_NoData() {
		ByteArray returned = provider.getSHA1Hash(new ByteArray());
		assertEquals("da39a3ee5e6b4b0d3255bfef95601890afd80709", Hexify.hexify(returned));
	}

	////////////////////////////////////////////////////////////
	// MD5
	////////////////////////////////////////////////////////////

	@Test
	public void testGetMD5Hash_Empty() {
		Hash md5Hash = provider.createMD5();
		md5Hash = md5Hash.update(new ByteArray(""));
		assertEquals("d41d8cd98f00b204e9800998ecf8427e", Hexify.hexify(md5Hash.getHash()));
	}

	@Test
	public void testGetMD5Hash_Alphabet() {
		Hash md5Hash = provider.createMD5();
		md5Hash = md5Hash.update(new ByteArray("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"));
		assertEquals("d174ab98d277d9f5a5611c2c9f419d9f", Hexify.hexify(md5Hash.getHash()));
	}

	@Test
	public void testMD5Incremental() {
		Hash md5Hash = provider.createMD5();
		md5Hash = md5Hash.update(new ByteArray("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
		md5Hash = md5Hash.update(new ByteArray("abcdefghijklmnopqrstuvwxyz0123456789"));
		assertEquals("d174ab98d277d9f5a5611c2c9f419d9f", Hexify.hexify(md5Hash.getHash()));
	}

	////////////////////////////////////////////////////////////
	// HMAC-SHA1
	////////////////////////////////////////////////////////////

	@Test
	public void testGetHMACSHA1() {
		ByteArray returned = provider.getHMACSHA1(new ByteArray("foo"), new ByteArray("foobar"));
		assertEquals("a4eeba8e633d778869f568d05a1b3dc72bfd04dd", Hexify.hexify(returned));
	}

	@Test
	public void testGetHMACSHA1_KeyLongerThanBlockSize() {
		ByteArray returned = provider.getHMACSHA1(new ByteArray("---------|---------|---------|---------|---------|----------|---------|"), new ByteArray("foobar"));
		assertEquals("d66e8f507c31d32c0620b9e367678ecf205d2b0a", Hexify.hexify(returned));
	}
}
