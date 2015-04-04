/*
 * Copyright (c) 2011 Vlad Voicu
 * Licensed under the Simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
/*
* Copyright (c) 2015 Thomas Graviou
* Licensed under the Simplified BSD license.
* See Documentation/Licenses/BSD-simplified.txt for more information.
*/
 package com.isode.stroke.serializer.payloadserializers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import com.isode.stroke.elements.Replace;
import com.isode.stroke.serializer.payloadserializers.ReplaceSerializer;

public class ReplaceSerializerTest {
	
	@Test
	public void testSerialize() {
		ReplaceSerializer testling = new ReplaceSerializer();
		Replace replace = new Replace();
		replace.setID("bad1");
		String expected = "<replace id = 'bad1' xmlns='urn:xmpp:message-correct:0'/>";
		assertEquals(expected, testling.serialize(replace));
	}
	
}
