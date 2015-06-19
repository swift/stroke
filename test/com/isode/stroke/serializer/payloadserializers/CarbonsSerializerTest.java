/*
 * Copyright (c) 2015 Isode Limited.
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
import com.isode.stroke.elements.Message;
import com.isode.stroke.elements.Thread;
import com.isode.stroke.elements.Forwarded;
import com.isode.stroke.elements.CarbonsEnable;
import com.isode.stroke.elements.CarbonsDisable;
import com.isode.stroke.elements.CarbonsReceived;
import com.isode.stroke.elements.CarbonsSent;
import com.isode.stroke.elements.CarbonsPrivate;
import com.isode.stroke.serializer.payloadserializers.FullPayloadSerializerCollection;
import com.isode.stroke.serializer.payloadserializers.CarbonsEnableSerializer;
import com.isode.stroke.serializer.payloadserializers.CarbonsDisableSerializer;
import com.isode.stroke.serializer.payloadserializers.CarbonsReceivedSerializer;
import com.isode.stroke.serializer.payloadserializers.CarbonsSentSerializer;
import com.isode.stroke.serializer.payloadserializers.CarbonsPrivateSerializer;
import com.isode.stroke.jid.JID;

public class CarbonsSerializerTest {

	private FullPayloadSerializerCollection serializers = new FullPayloadSerializerCollection();

	/**
	* Default Constructor.
	*/
	public CarbonsSerializerTest() {

	}

	/*
	 * Test serializing of example 3 in XEP-0280.
	 */
	@Test
	public void testSerializeExample3() {
		CarbonsEnableSerializer testling = new CarbonsEnableSerializer();

		assertEquals("<enable xmlns=\"urn:xmpp:carbons:2\"/>", testling.serialize(new CarbonsEnable()));
	}

	/*
	 * Test serializing of example 6 in XEP-0280.
	 */
	@Test
	public void testSerializeExample6() {
		CarbonsDisableSerializer testling = new CarbonsDisableSerializer();

		assertEquals("<disable xmlns=\"urn:xmpp:carbons:2\"/>", testling.serialize(new CarbonsDisable()));
	}

	/*
	 * Test serializing of example 12 in XEP-0280.
	 */
	@Test
	public void testSerializeExample12() {
		CarbonsReceivedSerializer testling = new CarbonsReceivedSerializer(serializers);

		CarbonsReceived received = new CarbonsReceived();

		Forwarded forwarded = new Forwarded();

		Message message = new Message();
		message.setFrom(new JID("juliet@capulet.example/balcony"));
		message.setTo(new JID("romeo@montague.example/garden"));
		message.setBody("What man art thou that, thus bescreen'd in night, so stumblest on my counsel?");
		message.addPayload(new Thread("0e3141cd80894871a68e6fe6b1ec56fa"));

		forwarded.setStanza(message);
		received.setForwarded(forwarded);

		assertEquals(
			"<received xmlns=\"urn:xmpp:carbons:2\">" +
				"<forwarded xmlns=\"urn:xmpp:forward:0\">" +
					"<message from=\"juliet@capulet.example/balcony\"" +
						" to=\"romeo@montague.example/garden\"" +
						" type=\"chat\">" +
						"<body>What man art thou that, thus bescreen'd in night, so stumblest on my counsel?</body>" +
						"<thread>0e3141cd80894871a68e6fe6b1ec56fa</thread>" +
					"</message>" +
				"</forwarded>" +
			"</received>", testling.serialize(received));
	}

	/*
	 * Test serializing of example 14 in XEP-0280.
	 */
	@Test
	public void testSerializeExample14() {
		CarbonsSentSerializer testling = new CarbonsSentSerializer(serializers);

		CarbonsSent sent = new CarbonsSent();

		Forwarded forwarded = new Forwarded();

		Message message = new Message();
		message.setTo(new JID("juliet@capulet.example/balcony"));
		message.setFrom(new JID("romeo@montague.example/home"));
		message.setBody("Neither, fair saint, if either thee dislike.");
		message.addPayload(new Thread("0e3141cd80894871a68e6fe6b1ec56fa"));

		forwarded.setStanza(message);
		sent.setForwarded(forwarded);

		assertEquals(
			"<sent xmlns=\"urn:xmpp:carbons:2\">" +
				"<forwarded xmlns=\"urn:xmpp:forward:0\">" +
					"<message from=\"romeo@montague.example/home\"" +
						" to=\"juliet@capulet.example/balcony\"" +
						" type=\"chat\">" +
						"<body>Neither, fair saint, if either thee dislike.</body>" +
						"<thread>0e3141cd80894871a68e6fe6b1ec56fa</thread>" +
					"</message>" +
				"</forwarded>" +
			"</sent>", testling.serialize(sent));
	}

	/*
	 * Test serializing of example 15 in XEP-0280.
	 */
	@Test
	public void testSerializeExample15() {
		CarbonsPrivateSerializer testling = new CarbonsPrivateSerializer();

		assertEquals("<private xmlns=\"urn:xmpp:carbons:2\"/>", testling.serialize(new CarbonsPrivate()));
	}
}