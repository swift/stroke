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
import com.isode.stroke.serializer.payloadserializers.DiscoInfoSerializer;
import com.isode.stroke.elements.DiscoInfo;
import com.isode.stroke.elements.Form;

public class DiscoInfoSerializerTest {

	/**
	* Default Constructor.
	*/
	public DiscoInfoSerializerTest() {

	}

	@Test
	public void testSerialize() {
		DiscoInfoSerializer testling = new DiscoInfoSerializer();
		DiscoInfo discoInfo = new DiscoInfo();
		discoInfo.addIdentity(new DiscoInfo.Identity("Swift", "client", "pc"));
		discoInfo.addIdentity(new DiscoInfo.Identity("Vlug", "client", "pc", "nl"));
		discoInfo.addFeature("http://jabber.org/protocol/caps");
		discoInfo.addFeature("http://jabber.org/protocol/disco#info");
		discoInfo.setNode("http://swift.im#bla");

		String expectedResult = 
			"<query node=\"http://swift.im#bla\" xmlns=\"http://jabber.org/protocol/disco#info\">" +
				"<identity category=\"client\" name=\"Swift\" type=\"pc\"/>" +
				"<identity category=\"client\" name=\"Vlug\" type=\"pc\" xml:lang=\"nl\"/>" +
				"<feature var=\"http://jabber.org/protocol/caps\"/>" +
				"<feature var=\"http://jabber.org/protocol/disco#info\"/>" +
			"</query>";

		assertEquals(expectedResult, testling.serialize(discoInfo));
	}

	@Test
	public void testSerialize_Form() {
		DiscoInfoSerializer testling = new DiscoInfoSerializer();
		DiscoInfo discoInfo = new DiscoInfo();
		discoInfo.addFeature("http://jabber.org/protocol/caps");
		discoInfo.addFeature("http://jabber.org/protocol/disco#info");
		Form form = new Form(Form.Type.FORM_TYPE);
		form.setTitle("Bot Configuration");
		discoInfo.addExtension(form);

		String expectedResult = 
			"<query xmlns=\"http://jabber.org/protocol/disco#info\">" +
				"<feature var=\"http://jabber.org/protocol/caps\"/>" +
				"<feature var=\"http://jabber.org/protocol/disco#info\"/>" +
				"<x type=\"form\" xmlns=\"jabber:x:data\">" +
							"<title>Bot Configuration</title>" +
				"</x>" +
			"</query>";

		assertEquals(expectedResult, testling.serialize(discoInfo));
	}
}