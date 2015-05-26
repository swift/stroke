/*
 * Copyright (c) 2012 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;
import com.isode.stroke.base.URL;

public class URLTest {

	@Test
	public void testFromString() {
		URL url = URL.fromString("http://foo.bar/baz/bam");

		assertEquals("http", url.getScheme());
		assertEquals("foo.bar", url.getHost());
		assertTrue(url.getPort() == null);
		assertEquals("/baz/bam", url.getPath());
	}

	@Test
	public void testFromString_WithoutPath() {
		URL url = URL.fromString("http://foo.bar");

		assertEquals("http", url.getScheme());
		assertEquals("foo.bar", url.getHost());
		assertTrue(url.getPort() == null);
		assertEquals("", url.getPath());
	}

	@Test
	public void testFromString_WithRootPath() {
		URL url = URL.fromString("http://foo.bar/");

		assertEquals("http", url.getScheme());
		assertEquals("foo.bar", url.getHost());
		assertTrue(url.getPort() == null);
		assertEquals("/", url.getPath());
	}

	@Test
	public void testFromString_WithPort() {
		URL url = URL.fromString("http://foo.bar:1234/baz/bam");

		assertEquals("http", url.getScheme());
		assertEquals("foo.bar", url.getHost());
		assertEquals(Integer.valueOf(1234), url.getPort());
		assertEquals("/baz/bam", url.getPath());
	}

	@Test
	public void testFromString_WithPortOnePartPath() {
		URL url = URL.fromString("http://foo.bar:11440/http-bind/");

		assertEquals("http", url.getScheme());
		assertEquals("foo.bar", url.getHost());
		assertEquals(Integer.valueOf(11440), url.getPort());
		assertEquals("/http-bind/", url.getPath());
	}

	@Test
	public void testFromString_WithPortWithoutPath() {
		URL url = URL.fromString("http://foo.bar:1234");

		assertEquals("http", url.getScheme());
		assertEquals("foo.bar", url.getHost());
		assertEquals(Integer.valueOf(1234), url.getPort());
		assertEquals("", url.getPath());
	}

	@Test
	public void testFromString_WithUserInfo() {
		URL url = URL.fromString("http://user:pass@foo.bar/baz/bam");

		assertEquals("http", url.getScheme());
		assertEquals("foo.bar", url.getHost());
		assertEquals("/baz/bam", url.getPath());
	}


	@Test
	public void testFromString_NonASCIIHost() {
		URL url = URL.fromString("http://www.tron%C3%A7on.be/baz/bam");

		assertEquals("www.tron\u00E7on.be", url.getHost());
	}

	@Test
	public void testFromString_NonASCIIPath() {
		URL url = URL.fromString("http://foo.bar/baz/tron%C3%A7on/bam");

		assertEquals("/baz/tron\u00E7on/bam", url.getPath());
	}

	@Test
	public void testToString() {
		assertEquals("http://foo.bar/baz/bam", new URL("http", "foo.bar", "/baz/bam").toString());
	}

	@Test
	public void testToString_WithPort() {
		assertEquals("http://foo.bar:1234/baz/bam", new URL("http", "foo.bar", 1234, "/baz/bam").toString());
	}
}