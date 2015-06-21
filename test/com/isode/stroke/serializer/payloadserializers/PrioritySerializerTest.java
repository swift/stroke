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
import com.isode.stroke.serializer.payloadserializers.PrioritySerializer;
import com.isode.stroke.serializer.PayloadSerializerCollection;
import com.isode.stroke.elements.Priority;

public class PrioritySerializerTest {

	/**
	* Default Constructor.
	*/
	public PrioritySerializerTest() {

	}

	@Test
	public void testSerialize() {
		PrioritySerializer testling = new PrioritySerializer();
		Priority priority = new Priority(-113);

		assertEquals("<priority>-113</priority>", testling.serialize(priority));
	}
}