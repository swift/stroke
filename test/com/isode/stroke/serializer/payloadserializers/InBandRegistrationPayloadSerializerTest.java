/*
 * Copyright (c) 2010-2013 Isode Limited.
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
import com.isode.stroke.serializer.payloadserializers.InBandRegistrationPayloadSerializer;
import com.isode.stroke.elements.InBandRegistrationPayload;
import com.isode.stroke.elements.Form;
import com.isode.stroke.elements.FormField;

public class InBandRegistrationPayloadSerializerTest {

	/**
	* Default Constructor.
	*/
	public InBandRegistrationPayloadSerializerTest() {

	}

	@Test
	public void testSerialize() {
		InBandRegistrationPayloadSerializer testling = new InBandRegistrationPayloadSerializer();
		InBandRegistrationPayload registration = new InBandRegistrationPayload();
		registration.setRegistered(true);

		String expectedResult = 
			"<query xmlns=\"jabber:iq:register\">" +
				"<registered/>" +
			"</query>";

		assertEquals(expectedResult, testling.serialize(registration));
	}

	@Test
	public void testSerialize_Form() {
		InBandRegistrationPayloadSerializer testling = new InBandRegistrationPayloadSerializer();
		InBandRegistrationPayload registration = new InBandRegistrationPayload();
		registration.setInstructions("Use the enclosed form to register.");

		Form form = new Form();
		form.setTitle("Contest Registration");

		FormField field = new FormField(FormField.Type.HIDDEN_TYPE, "jabber:iq:register");
		field.setName("FORM_TYPE");
		form.addField(field);
		registration.setForm(form);

		String expectedResult = 
			"<query xmlns=\"jabber:iq:register\">"
			+		"<instructions>Use the enclosed form to register.</instructions>"
			+		"<x type=\"form\" xmlns=\"jabber:x:data\">"
			+			"<title>Contest Registration</title>"
			+			"<field type=\"hidden\" var=\"FORM_TYPE\">"
			+				"<value>jabber:iq:register</value>"
			+			"</field>"
			+		"</x>"
			+	"</query>";

		assertEquals(expectedResult, testling.serialize(registration));
	}
}