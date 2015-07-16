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

package com.isode.stroke.serializer;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.isode.stroke.serializer.StreamFeaturesSerializer;
import com.isode.stroke.elements.StreamFeatures;
import com.isode.stroke.base.SafeByteArray;

public class StreamFeaturesSerializerTest {

	/**
	* Default Constructor.
	*/
	public StreamFeaturesSerializerTest() {

	}

	@Test
	public void testSerialize() {
		StreamFeaturesSerializer testling = new StreamFeaturesSerializer();
		StreamFeatures streamFeatures = new StreamFeatures();
		streamFeatures.setHasStartTLS();
		streamFeatures.addCompressionMethod("zlib");
		streamFeatures.addCompressionMethod("lzw");
		streamFeatures.addAuthenticationMechanism("DIGEST-MD5");
		streamFeatures.addAuthenticationMechanism("PLAIN");
		streamFeatures.setHasResourceBind();
		streamFeatures.setHasSession();
		streamFeatures.setHasStreamManagement();
		streamFeatures.setHasRosterVersioning();

		assertEquals(
		new SafeByteArray("<stream:features>"
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
	+		"<session xmlns=\"urn:ietf:params:xml:ns:xmpp-session\"/>"
	+		"<sm xmlns=\"urn:xmpp:sm:2\"/>"
	+		"<ver xmlns=\"urn:xmpp:features:rosterver\"/>"
	+	"</stream:features>"), testling.serialize(streamFeatures));
	}
}