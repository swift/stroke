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
import com.isode.stroke.serializer.payloadserializers.StatusSerializer;
import com.isode.stroke.serializer.PayloadSerializerCollection;
import com.isode.stroke.elements.Status;

public class StatusSerializerTest {

	/**
	* Default Constructor.
	*/
	public StatusSerializerTest() {

	}

	@Test
	public void testSerialize() {
		StatusSerializer testling = new StatusSerializer();
		Status status = new Status("I am away");
		String expectedResult = "<status>I am away</status>";
		assertEquals(expectedResult, testling.serialize(status));
	}
}