/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.parser.payloadparsers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import com.isode.stroke.elements.InBandRegistrationPayload;
import com.isode.stroke.elements.Form;
import com.isode.stroke.parser.payloadparsers.InBandRegistrationPayloadParser;
import com.isode.stroke.parser.payloadparsers.PayloadsParserTester;
import com.isode.stroke.eventloop.DummyEventLoop;

public class InBandRegistrationPayloadParserTest {

	public InBandRegistrationPayloadParserTest() {

	}

	@Test
	public void testParse() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse("<query xmlns=\"jabber:iq:register\">" +
								"<registered/>" +
								"</query>"));

		InBandRegistrationPayload payload = (InBandRegistrationPayload)parser.getPayload();
		assertNotNull(payload);
		assertTrue(payload.isRegistered());
	}

	@Test
	public void testParse_Form() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse("<query xmlns=\"jabber:iq:register\">" +
									"<instructions>Use the enclosed form to register.</instructions>" +
									"<x type=\"form\" xmlns=\"jabber:x:data\">" +
									"<title>Contest Registration</title>" +
									"<field type=\"hidden\" var=\"FORM_TYPE\">" +
									"<value>jabber:iq:register</value>" +
									"</field>" +
									"</x>" +
									"</query>"));

		InBandRegistrationPayload payload = (InBandRegistrationPayload)parser.getPayload();
		assertNotNull(payload);
		assertEquals("Use the enclosed form to register.", payload.getInstructions());

		Form form = payload.getForm();
		assertNotNull(form);
		assertEquals("Contest Registration", form.getTitle());
		assertEquals(Form.Type.FORM_TYPE, form.getType());
		assertEquals("jabber:iq:register", form.getFormType());

	}
}