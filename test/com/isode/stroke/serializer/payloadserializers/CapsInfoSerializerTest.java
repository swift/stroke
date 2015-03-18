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

import org.junit.BeforeClass;
import org.junit.Test;
import com.isode.stroke.elements.CapsInfo;
import com.isode.stroke.serializer.payloadserializers.CapsInfoSerializer;

public class CapsInfoSerializerTest {

	@Test
	public void testSerialize() {
		CapsInfoSerializer testling = new CapsInfoSerializer();
		CapsInfo priority = new CapsInfo("http://swift.im", "myversion", "sha-1");
		String expected = "<c hash=\"sha-1\" node=\"http://swift.im\" ver=\"myversion\" xmlns=\"http://jabber.org/protocol/caps\"/>";
		assertEquals(expected, testling.serialize(priority));
	}
}