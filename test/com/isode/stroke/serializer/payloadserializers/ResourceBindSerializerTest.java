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
import com.isode.stroke.serializer.payloadserializers.ResourceBindSerializer;
import com.isode.stroke.elements.ResourceBind;
import com.isode.stroke.jid.JID;

public class ResourceBindSerializerTest {

	/**
	* Default Constructor.
	*/
	public ResourceBindSerializerTest() {

	}

	@Test
	public void testSerialize_JID() {
		ResourceBindSerializer testling = new ResourceBindSerializer();
		ResourceBind resourceBind = new ResourceBind();
		resourceBind.setJID(new JID("somenode@example.com/someresource"));

		assertEquals(
			"<bind xmlns=\"urn:ietf:params:xml:ns:xmpp-bind\">" +
				"<jid>somenode@example.com/someresource</jid>" +
			"</bind>", testling.serialize(resourceBind));
	}

	@Test
	public void testSerialize_Resource() {
		ResourceBindSerializer testling = new ResourceBindSerializer();
		ResourceBind resourceBind = new ResourceBind();
		resourceBind.setResource("someresource");

		assertEquals(
			"<bind xmlns=\"urn:ietf:params:xml:ns:xmpp-bind\">" +
				"<resource>someresource</resource>" +
			"</bind>", testling.serialize(resourceBind));
	}

	@Test
	public void testSerialize_Empty() {
		ResourceBindSerializer testling = new ResourceBindSerializer();
		ResourceBind resourceBind = new ResourceBind();

		assertEquals("<bind xmlns=\"urn:ietf:params:xml:ns:xmpp-bind\"/>", testling.serialize(resourceBind));
	}
}