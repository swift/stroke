/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.serializer.payloadserializers;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.isode.stroke.serializer.payloadserializers.IsodeIQDelegationSerializer;
import com.isode.stroke.serializer.PayloadSerializerCollection;
import com.isode.stroke.elements.IsodeIQDelegation;
import com.isode.stroke.base.DateTime;
import com.isode.stroke.elements.Delay;
import com.isode.stroke.elements.Forwarded;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.elements.Message;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.elements.Presence;
import com.isode.stroke.elements.Subject;
import com.isode.stroke.jid.JID;

public class IsodeIQDelegationSerializerTest {

	/**
	* Default Constructor.
	*/
	public IsodeIQDelegationSerializerTest() {

	}

	@Test
	public void testSerialize_Forwarded_IQ() {
		FullPayloadSerializerCollection serializerCollection = new FullPayloadSerializerCollection();
		IsodeIQDelegationSerializer testling = new IsodeIQDelegationSerializer(serializerCollection);
		IsodeIQDelegation isodeIQDelegation = new IsodeIQDelegation();
		
		IQ iq = IQ.createResult(JID.fromString("juliet@capulet.lit/balcony"), JID.fromString("romeo@montague.lit/orchard"), "id0", new Subject("text"));
		Forwarded forwarded = new Forwarded();
		forwarded.setStanza(iq);
		forwarded.setDelay(new Delay(DateTime.stringToDate("2010-07-10T23:08:25Z"), null));
		isodeIQDelegation.setForward(forwarded);
		String expectedResult = "<delegate xmlns=\"http://isode.com/iq_delegation\">" +
								"<forwarded xmlns=\"urn:xmpp:forward:0\">" +
								"<delay stamp=\"2010-07-10T23:08:25Z\" xmlns=\"urn:xmpp:delay\"/>" +
								"<iq from=\"romeo@montague.lit/orchard\" id=\"id0\" to=\"juliet@capulet.lit/balcony\" type=\"result\"><subject>text</subject></iq>" +
								"</forwarded>" +
								"</delegate>";
		assertEquals(expectedResult, testling.serialize(isodeIQDelegation));
	}

	@Test
	public void testSerialize_Forwarded_Message() {
		FullPayloadSerializerCollection serializerCollection = new FullPayloadSerializerCollection();
		IsodeIQDelegationSerializer testling = new IsodeIQDelegationSerializer(serializerCollection);
		IsodeIQDelegation isodeIQDelegation = new IsodeIQDelegation();

		Message message = new Message();
		message.setType(Message.Type.Chat);
		message.setTo(JID.fromString("juliet@capulet.lit/balcony"));
		message.setFrom(JID.fromString("romeo@montague.lit/orchard"));
		message.setBody("Call me but love, and I'll be new baptized; Henceforth I never will be Romeo.");

		Forwarded forwarded = new Forwarded();
		forwarded.setStanza(message);
		forwarded.setDelay(new Delay(DateTime.stringToDate("2010-07-10T23:08:25Z"), null));

		isodeIQDelegation.setForward(forwarded);

		String expectedResult = 
			"<delegate xmlns=\"http://isode.com/iq_delegation\">"
		  +	"<forwarded xmlns=\"urn:xmpp:forward:0\">"
		  +     "<delay stamp=\"2010-07-10T23:08:25Z\" xmlns=\"urn:xmpp:delay\"/>"
		  +     "<message from=\"romeo@montague.lit/orchard\" to=\"juliet@capulet.lit/balcony\" type=\"chat\">"
		  +         "<body>Call me but love, and I'll be new baptized; Henceforth I never will be Romeo.</body>"
		  +     "</message>"
		  + "</forwarded>"
		  +	"</delegate>";

		assertEquals(expectedResult, testling.serialize(isodeIQDelegation));
	}

	@Test
	public void testSerialize_Forwarded_MessageNoDelay() {
		FullPayloadSerializerCollection serializerCollection = new FullPayloadSerializerCollection();
		IsodeIQDelegationSerializer testling = new IsodeIQDelegationSerializer(serializerCollection);
		IsodeIQDelegation isodeIQDelegation = new IsodeIQDelegation();

		Message message = new Message();
		message.setType(Message.Type.Chat);
		message.setTo(JID.fromString("juliet@capulet.lit/balcony"));
		message.setFrom(JID.fromString("romeo@montague.lit/orchard"));
		message.setBody("Call me but love, and I'll be new baptized; Henceforth I never will be Romeo.");

		Forwarded forwarded = new Forwarded();
		forwarded.setStanza(message);

		isodeIQDelegation.setForward(forwarded);
		
		String expectedResult = 
			"<delegate xmlns=\"http://isode.com/iq_delegation\">"
		  +	"<forwarded xmlns=\"urn:xmpp:forward:0\">"
		  +     "<message from=\"romeo@montague.lit/orchard\" to=\"juliet@capulet.lit/balcony\" type=\"chat\">"
		  +         "<body>Call me but love, and I'll be new baptized; Henceforth I never will be Romeo.</body>"
		  +     "</message>"
		  + "</forwarded>"
		  + "</delegate>";
		assertEquals(expectedResult, testling.serialize(isodeIQDelegation));
	}

	@Test
	public void testSerialize_Forwarded_Presence() {
		FullPayloadSerializerCollection serializerCollection = new FullPayloadSerializerCollection();
		IsodeIQDelegationSerializer testling = new IsodeIQDelegationSerializer(serializerCollection);
		IsodeIQDelegation isodeIQDelegation = new IsodeIQDelegation();

		Presence presence = new Presence();
		presence.setType(Presence.Type.Subscribe);

		Forwarded forwarded = new Forwarded();
		forwarded.setStanza(presence);
		forwarded.setDelay(new Delay(DateTime.stringToDate("2010-07-10T23:08:25Z"), null));

		isodeIQDelegation.setForward(forwarded);

		String expectedResult = 
			"<delegate xmlns=\"http://isode.com/iq_delegation\">"
		  +	"<forwarded xmlns=\"urn:xmpp:forward:0\">"
		  +     "<delay stamp=\"2010-07-10T23:08:25Z\" xmlns=\"urn:xmpp:delay\"/>"
		  +     "<presence type=\"subscribe\"/>"
		  + "</forwarded>"
		  + "</delegate>";

		assertEquals(expectedResult, testling.serialize(isodeIQDelegation));
	}
}