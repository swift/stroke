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

package com.isode.stroke.serializer.payloadserializers;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.isode.stroke.serializer.payloadserializers.StreamInitiationSerializer;
import com.isode.stroke.serializer.PayloadSerializerCollection;
import com.isode.stroke.elements.StreamInitiation;
import com.isode.stroke.elements.StreamInitiationFileInfo;

public class StreamInitiationSerializerTest {

	/**
	* Default Constructor.
	*/
	public StreamInitiationSerializerTest() {

	}

	@Test
	public void testSerialize_Request() {
		StreamInitiationSerializer testling = new StreamInitiationSerializer();
		StreamInitiation streamInitiation = new StreamInitiation();
		StreamInitiationFileInfo fileInfo = new StreamInitiationFileInfo("test.txt", "This is info about the file.", 1022);
		streamInitiation.setID("a0");
		streamInitiation.setFileInfo(fileInfo);
		streamInitiation.addProvidedMethod("http://jabber.org/protocol/bytestreams");
		streamInitiation.addProvidedMethod("jabber:iq:oob");
		streamInitiation.addProvidedMethod("http://jabber.org/protocol/ibb");
		String expectedResult = "<si id=\"a0\" profile=\"http://jabber.org/protocol/si/profile/file-transfer\" xmlns=\"http://jabber.org/protocol/si\">"
				+		"<file name=\"test.txt\" size=\"1022\" xmlns=\"http://jabber.org/protocol/si/profile/file-transfer\">"
				+			"<desc>This is info about the file.</desc>"
				+		"</file>"
				+		"<feature xmlns=\"http://jabber.org/protocol/feature-neg\">"
				+			"<x type=\"form\" xmlns=\"jabber:x:data\">"
				+				"<field type=\"list-single\" var=\"stream-method\">"
				+					"<option><value>http://jabber.org/protocol/bytestreams</value></option>"
				+					"<option><value>jabber:iq:oob</value></option>"
				+					"<option><value>http://jabber.org/protocol/ibb</value></option>"
				+				"</field>"
				+			"</x>"
				+		"</feature>"
				+	"</si>";
		assertEquals(expectedResult, testling.serialize(streamInitiation));
	}

	@Test
	public void testSerialize_Response() {
		StreamInitiationSerializer testling = new StreamInitiationSerializer();
		StreamInitiation streamInitiation = new StreamInitiation();
		streamInitiation.setRequestedMethod("http://jabber.org/protocol/bytestreams");
		String expectedResult = "<si profile=\"http://jabber.org/protocol/si/profile/file-transfer\" xmlns=\"http://jabber.org/protocol/si\">"
				+		"<feature xmlns=\"http://jabber.org/protocol/feature-neg\">"
				+			"<x type=\"submit\" xmlns=\"jabber:x:data\">"
				+				"<field type=\"list-single\" var=\"stream-method\">"
				+					"<value>http://jabber.org/protocol/bytestreams</value>"
				+				"</field>"
				+			"</x>"
				+		"</feature>"
				+	"</si>";
		assertEquals(expectedResult, testling.serialize(streamInitiation));
	}
}