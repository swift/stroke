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
import com.isode.stroke.serializer.payloadserializers.SoftwareVersionSerializer;
import com.isode.stroke.elements.SoftwareVersion;

public class SoftwareVersionSerializerTest {

	/**
	* Default Constructor.
	*/
	public SoftwareVersionSerializerTest() {

	}

	@Test
	public void testSerialize() {
		SoftwareVersionSerializer testling = new SoftwareVersionSerializer();
		SoftwareVersion softwareVersion = new SoftwareVersion("Swift", "0.1", "Mac OS X");
		String expectedResult = "<query xmlns=\"jabber:iq:version\"><name>Swift</name><version>0.1</version><os>Mac OS X</os></query>";
		assertEquals(expectedResult, testling.serialize(softwareVersion));
	}
}