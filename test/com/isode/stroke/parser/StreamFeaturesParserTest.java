/*
 * Copyright (c) 2013 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import com.isode.stroke.parser.StreamFeaturesParser;
import com.isode.stroke.elements.StreamFeatures;
import com.isode.stroke.parser.ElementParserTester;

public class StreamFeaturesParserTest {

	public StreamFeaturesParserTest() {

	}

	@Test
	public void testParse() {
		StreamFeaturesParser testling = new StreamFeaturesParser();
		ElementParserTester parser = new ElementParserTester(testling);

		assertTrue(parser.parse(
			"<stream:features xmlns:stream='http://etherx.jabber.org/streams'>"
		+		"<starttls xmlns=\"urn:ietf:params:xml:ns:xmpp-tls\"/>"
		+		"<compression xmlns=\"http://jabber.org/features/compress\">"
		+			"<method>zlib</method>"
		+			"<method>lzw</method>"
		+		"</compression>"
		+		"<mechanisms xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">"
		+			"<mechanism>DIGEST-MD5</mechanism>"
		+			"<mechanism>PLAIN</mechanism>"
		+		"</mechanisms>"
		+		"<bind xmlns=\"urn:ietf:params:xml:ns:xmpp-bind\"/>"
		+		"<sm xmlns='urn:xmpp:sm:2'/>"
		+		"<session xmlns=\"urn:ietf:params:xml:ns:xmpp-session\"/>"
		+		"<ver xmlns=\"urn:xmpp:features:rosterver\"/>"
		+	"</stream:features>"));

		StreamFeatures element = (StreamFeatures)(testling.getElement());
		assertTrue(element.hasStartTLS());
		assertTrue(element.hasSession());
		assertTrue(element.hasResourceBind());
		assertTrue(element.hasCompressionMethod("zlib"));
		assertTrue(element.hasCompressionMethod("lzw"));
		assertTrue(element.hasAuthenticationMechanisms());
		assertTrue(element.hasAuthenticationMechanism("DIGEST-MD5"));
		assertTrue(element.hasAuthenticationMechanism("PLAIN"));
		assertNull(element.getAuthenticationHostname());		
		assertTrue(element.hasStreamManagement());
		assertTrue(element.hasRosterVersioning());
	}

	@Test
	public void testParse_Empty() {
		StreamFeaturesParser testling = new StreamFeaturesParser();
		ElementParserTester parser = new ElementParserTester(testling);

		assertTrue(parser.parse("<stream:features xmlns:stream='http://etherx.jabber.org/streams'/>"));

		StreamFeatures element = (StreamFeatures)(testling.getElement());
		assertFalse(element.hasStartTLS());
		assertFalse(element.hasSession());
		assertFalse(element.hasResourceBind());
		assertFalse(element.hasAuthenticationMechanisms());
	}


	@Test
	public void testParse_AuthenticationHostname() {
		StreamFeaturesParser testling = new StreamFeaturesParser();
		ElementParserTester parser = new ElementParserTester(testling);
		String hostname = "auth42.us.example.com";

		assertTrue(parser.parse(
			"<stream:features xmlns:stream='http://etherx.jabber.org/streams'>"
		+		"<mechanisms xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">"
		+			"<mechanism>GSSAPI</mechanism>"
		+			"<hostname xmlns=\"urn:xmpp:domain-based-name:1\">auth42.us.example.com</hostname>"
		+		"</mechanisms>"
		+	"</stream:features>"));

		StreamFeatures element = (StreamFeatures)(testling.getElement());
		assertTrue(element.hasAuthenticationMechanism("GSSAPI"));
		assertEquals(element.getAuthenticationHostname(), hostname);
	}


	@Test
	public void testParse_AuthenticationHostnameEmpty() {
		StreamFeaturesParser testling = new StreamFeaturesParser();
		ElementParserTester parser = new ElementParserTester(testling);

		assertTrue(parser.parse(
			"<stream:features xmlns:stream='http://etherx.jabber.org/streams'>"
		+		"<mechanisms xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">"
		+			"<mechanism>GSSAPI</mechanism>"
		+			"<hostname xmlns=\"urn:xmpp:domain-based-name:1\"></hostname>"
		+		"</mechanisms>"
		+	"</stream:features>"));

		StreamFeatures element = (StreamFeatures)(testling.getElement());
		assertTrue(element.hasAuthenticationMechanism("GSSAPI"));
		assertTrue(element.getAuthenticationHostname().isEmpty());
	}
}