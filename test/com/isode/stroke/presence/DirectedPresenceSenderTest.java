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
import com.isode.stroke.client.DummyStanzaChannel;
import com.isode.stroke.presence.DirectedPresenceSender;
import com.isode.stroke.presence.StanzaChannelPresenceSender;
import com.isode.stroke.jid.JID;

public class DirectedPresenceSenderTest {

	private DummyStanzaChannel channel;
	private StanzaChannelPresenceSender stanzaChannelPresenceSender;
	private Presence testPresence;
	private Presence secondTestPresence;

	private DirectedPresenceSender createPresenceSender() {
		return new DirectedPresenceSender(stanzaChannelPresenceSender);
	}

	@Before
	public void setUp() {
		channel = new DummyStanzaChannel();
		testPresence = new Presence();
		testPresence.setStatus("Foo");
		secondTestPresence = new Presence();
		secondTestPresence.setStatus("Bar");
		stanzaChannelPresenceSender = new StanzaChannelPresenceSender(channel);
	}

	@Test
	public void testSendPresence() {
		DirectedPresenceSender testling = createPresenceSender();
		testling.sendPresence(testPresence);

		assertEquals(1, (channel.sentStanzas.size()));
		Presence presence = (Presence)(channel.sentStanzas.get(0));
		assertEquals(testPresence, presence);
	}

	@Test
	public void testSendPresence_UndirectedPresenceWithDirectedPresenceReceivers() {
		DirectedPresenceSender testling = createPresenceSender();
		testling.addDirectedPresenceReceiver(new JID("alice@wonderland.lit/teaparty"), DirectedPresenceSender.SendPresence.AndSendPresence);

		testling.sendPresence(testPresence);

		assertEquals(2, (channel.sentStanzas.size()));
		Presence presence = (Presence)(channel.sentStanzas.get(0));
		assertEquals(testPresence, presence);
		presence = (Presence)(channel.sentStanzas.get(1));
		assertEquals(testPresence.getStatus(), presence.getStatus());
		assertEquals(new JID("alice@wonderland.lit/teaparty"), presence.getTo());
	}

	@Test
	public void testSendPresence_DirectedPresenceWithDirectedPresenceReceivers() {
		DirectedPresenceSender testling = createPresenceSender();
		testling.addDirectedPresenceReceiver(new JID("alice@wonderland.lit/teaparty"), DirectedPresenceSender.SendPresence.AndSendPresence);
		channel.sentStanzas.clear();

		testPresence.setTo(new JID("foo@bar.com"));
		testling.sendPresence(testPresence);

		assertEquals(1, (channel.sentStanzas.size()));
		Presence presence = (Presence)(channel.sentStanzas.get(0));
		assertEquals(testPresence, presence);
	}

	@Test
	public void testAddDirectedPresenceReceiver() {
		DirectedPresenceSender testling = createPresenceSender();
		testling.sendPresence(testPresence);
		channel.sentStanzas.clear();

		testling.addDirectedPresenceReceiver(new JID("alice@wonderland.lit/teaparty"), DirectedPresenceSender.SendPresence.AndSendPresence);

		assertEquals(1, (channel.sentStanzas.size()));
		Presence presence = (Presence)(channel.sentStanzas.get(0));
		assertEquals(testPresence.getStatus(), presence.getStatus());
		assertEquals(new JID("alice@wonderland.lit/teaparty"), presence.getTo());
	}

	@Test
	public void testAddDirectedPresenceReceiver_WithoutSendingPresence() {
		DirectedPresenceSender testling = createPresenceSender();
		testling.sendPresence(testPresence);
		channel.sentStanzas.clear();

		testling.addDirectedPresenceReceiver(new JID("alice@wonderland.lit/teaparty"), DirectedPresenceSender.SendPresence.DontSendPresence);

		assertEquals(0, (channel.sentStanzas.size()));
	}

	@Test
	public void testAddDirectedPresenceReceiver_AfterSendingDirectedPresence() {
		DirectedPresenceSender testling = createPresenceSender();
		testling.sendPresence(testPresence);
		secondTestPresence.setTo(new JID("foo@bar.com"));
		testling.sendPresence(secondTestPresence);
		channel.sentStanzas.clear();

		testling.addDirectedPresenceReceiver(new JID("alice@wonderland.lit/teaparty"), DirectedPresenceSender.SendPresence.AndSendPresence);

		assertEquals(1, (channel.sentStanzas.size()));
		Presence presence = (Presence)(channel.sentStanzas.get(0));
		assertEquals(testPresence.getStatus(), presence.getStatus());
		assertEquals(new JID("alice@wonderland.lit/teaparty"), presence.getTo());
	}

	@Test
	public void testRemoveDirectedPresenceReceiver() {
		DirectedPresenceSender testling = createPresenceSender();
		testling.addDirectedPresenceReceiver(new JID("alice@wonderland.lit/teaparty"), DirectedPresenceSender.SendPresence.DontSendPresence);

		testling.removeDirectedPresenceReceiver(new JID("alice@wonderland.lit/teaparty"), DirectedPresenceSender.SendPresence.AndSendPresence);
		testling.sendPresence(testPresence);

		assertEquals(2, (channel.sentStanzas.size()));
		assertEquals(((Presence)(channel.sentStanzas.get(0))).getType(), Presence.Type.Unavailable);
		assertEquals(channel.sentStanzas.get(1), testPresence);
	}

	@Test
	public void testRemoveDirectedPresenceReceiver_WithoutSendingPresence() {
		DirectedPresenceSender testling = createPresenceSender();
		testling.addDirectedPresenceReceiver(new JID("alice@wonderland.lit/teaparty"), DirectedPresenceSender.SendPresence.AndSendPresence);
		channel.sentStanzas.clear();

		testling.removeDirectedPresenceReceiver(new JID("alice@wonderland.lit/teaparty"), DirectedPresenceSender.SendPresence.DontSendPresence);
		testling.sendPresence(testPresence);

		assertEquals(1, (channel.sentStanzas.size()));
		assertEquals(channel.sentStanzas.get(0), testPresence);
	}
}