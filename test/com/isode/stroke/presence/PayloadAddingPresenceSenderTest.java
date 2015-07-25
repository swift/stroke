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

package com.isode.stroke.presence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Before;
import com.isode.stroke.elements.Presence;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.client.DummyStanzaChannel;
import com.isode.stroke.presence.DirectedPresenceSender;
import com.isode.stroke.presence.StanzaChannelPresenceSender;
import com.isode.stroke.presence.PayloadAddingPresenceSender;
import com.isode.stroke.jid.JID;

public class PayloadAddingPresenceSenderTest {

	private DummyStanzaChannel stanzaChannel;
	private StanzaChannelPresenceSender presenceSender;

	private PayloadAddingPresenceSender createSender() {
		PayloadAddingPresenceSender sender = new PayloadAddingPresenceSender(presenceSender);
		return sender;
	}

	private class MyPayload extends Payload {

		public MyPayload() {

		}

		public MyPayload(final String body) {
			this.body = body;
		}

		public String body = "";
	};

	@Before
	public void setUp() {
		stanzaChannel = new DummyStanzaChannel();
		presenceSender = new StanzaChannelPresenceSender(stanzaChannel);
	}

	@Test
	public void testSetPayloadAddsPayloadOnPresenceSend() {
		PayloadAddingPresenceSender testling = createSender();

		testling.setPayload(new MyPayload("foo"));
		testling.sendPresence(new Presence("bar"));

		assertEquals(1, (stanzaChannel.sentStanzas.size()));
		assertEquals(("bar"), stanzaChannel.getStanzaAtIndex(new Presence(), 0).getStatus());
		assertNotNull(stanzaChannel.getStanzaAtIndex(new Presence(), 0).getPayload(new MyPayload()));
	}

	@Test
	public void testSetNullPayloadDoesNotAddPayloadOnPresenceSend() {
		PayloadAddingPresenceSender testling = createSender();

		testling.setPayload(null);
		testling.sendPresence(new Presence("bar"));

		assertEquals(1, (stanzaChannel.sentStanzas.size()));
		assertEquals(("bar"), stanzaChannel.getStanzaAtIndex(new Presence(), 0).getStatus());
		assertNull(stanzaChannel.getStanzaAtIndex(new Presence(), 0).getPayload(new MyPayload()));
	}

	@Test
	public void testSendPresenceDoesNotAlterOriginalPayload() {
		PayloadAddingPresenceSender testling = createSender();

		testling.setPayload(new MyPayload("foo"));
		Presence presence = new Presence("bar");
		testling.sendPresence(presence);

		assertNull(presence.getPayload(new MyPayload()));
	}

	@Test
	public void testSetPayloadAfterInitialPresenceResendsPresence() {
		PayloadAddingPresenceSender testling = createSender();

		testling.sendPresence(new Presence("bar"));
		testling.setPayload(new MyPayload("foo"));

		assertEquals(2, (stanzaChannel.sentStanzas.size()));
		assertEquals(("bar"), stanzaChannel.getStanzaAtIndex(new Presence(), 1).getStatus());
		assertNotNull(stanzaChannel.getStanzaAtIndex(new Presence(), 1).getPayload(new MyPayload()));
	}

	@Test
	public void testSetPayloadAfterUnavailablePresenceDoesNotResendPresence() {
		PayloadAddingPresenceSender testling = createSender();

		testling.sendPresence(new Presence("bar"));

		Presence presence = new Presence("bar");
		presence.setType(Presence.Type.Unavailable);
		testling.sendPresence(presence);

		testling.setPayload(new MyPayload("foo"));

		assertEquals(2, (stanzaChannel.sentStanzas.size()));
	}

	@Test
	public void testSetPayloadAfterResetDoesNotResendPresence() {
		PayloadAddingPresenceSender testling = createSender();
		testling.sendPresence(new Presence("bar"));

		testling.reset();
		testling.setPayload(new MyPayload("foo"));

		assertEquals(1, (stanzaChannel.sentStanzas.size()));
	}

	@Test
	public void testSendDirectedPresenceIsNotResent() {
		PayloadAddingPresenceSender testling = createSender();

		testling.sendPresence(new Presence("bar"));
		Presence directedPresence = new Presence("baz");
		directedPresence.setTo(new JID("foo@bar.com"));
		testling.sendPresence(directedPresence);
		testling.setPayload(new MyPayload("foo"));

		assertEquals(3, (stanzaChannel.sentStanzas.size()));
		assertEquals(("bar"), stanzaChannel.getStanzaAtIndex(new Presence(), 2).getStatus());
	}
}