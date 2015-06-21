/*
 * Copyright (c) 2011 Vlad Voicu
 * Licensed under the Simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
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

package com.isode.stroke.serializer.payloadserializers;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.isode.stroke.serializer.payloadserializers.ReplaceSerializer;
import com.isode.stroke.serializer.PayloadSerializerCollection;
import com.isode.stroke.elements.Replace;

public class ReplaceSerializerTest {

	/**
	* Default Constructor.
	*/
	public ReplaceSerializerTest() {

	}

	@Test
	public void testSerialize() {
		ReplaceSerializer testling = new ReplaceSerializer();
		Replace replace = new Replace();
		replace.setID("bad1");
		assertEquals("<replace id = 'bad1' xmlns='urn:xmpp:message-correct:0'/>", testling.serialize(replace));
	}
}