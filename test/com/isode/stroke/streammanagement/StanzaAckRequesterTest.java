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

package com.isode.stroke.streammanagement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Before;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.elements.Presence;
import com.isode.stroke.elements.Message;
import com.isode.stroke.elements.Stanza;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.signals.Slot;
import java.util.Vector;

public class StanzaAckRequesterTest {

	private int acksRequested;
	private Vector<Stanza> ackedStanzas = new Vector<Stanza>();

	private Message createMessage(final String id) {
		Message result = new Message();
		result.setID(id);
		return result;
	}

	private IQ createIQ(final String id) {
		IQ result = new IQ();
		result.setID(id);
		return result;
	}

	private Presence createPresence(final String id) {
		Presence result = new Presence();
		result.setID(id);
		return result;
	}

	private StanzaAckRequester createRequester() {
		StanzaAckRequester requester = new StanzaAckRequester();
		requester.onRequestAck.connect(new Slot() {
			@Override
			public void call() {
				handleRequestAck();
			}
		});
		requester.onStanzaAcked.connect(new Slot1<Stanza>() {
			@Override
			public void call(Stanza s) {
				handleStanzaAcked(s);
			}
		});
		return requester;
	}

	private void handleRequestAck() {
		acksRequested++;
	}

	private void handleStanzaAcked(Stanza stanza) {
		ackedStanzas.add(stanza);
	}

	@Before
	public void setUp() {
		acksRequested = 0;
	}

	@Test
	public void testHandleStanzaSent_MessageRequestsAck() {
		StanzaAckRequester testling = createRequester();
		testling.handleStanzaSent(createMessage("m1"));

		assertEquals(1, acksRequested);
	}

	@Test
	public void testHandleStanzaSent_IQDoesNotRequestAck() {
		StanzaAckRequester testling = createRequester();
		testling.handleStanzaSent(createIQ("iq1"));

		assertEquals(0, acksRequested);
	}

	@Test
	public void testHandleStanzaSent_PresenceDoesNotRequestAck() {
		StanzaAckRequester testling = createRequester();
		testling.handleStanzaSent(createPresence("p1"));

		assertEquals(0, acksRequested);
	}

	@Test
	public void testHandleAckReceived_AcksStanza() {
		StanzaAckRequester testling = createRequester();
		testling.handleStanzaSent(createMessage("m1"));

		testling.handleAckReceived(1);

		assertEquals(1, (ackedStanzas.size()));
		assertEquals(("m1"), ackedStanzas.get(0).getID());
	}

	@Test
	public void testHandleAckReceived_AcksMultipleMessages() {
		StanzaAckRequester testling = createRequester();
		testling.handleStanzaSent(createMessage("m1"));
		testling.handleStanzaSent(createMessage("m2"));

		testling.handleAckReceived(2);

		assertEquals(2, (ackedStanzas.size()));
		assertEquals(("m1"), ackedStanzas.get(0).getID());
		assertEquals(("m2"), ackedStanzas.get(1).getID());
	}

	@Test
	public void testHandleAckReceived_AcksMultipleStanzas() {
		StanzaAckRequester testling = createRequester();
		testling.handleStanzaSent(createIQ("iq1"));
		testling.handleStanzaSent(createPresence("p1"));
		testling.handleStanzaSent(createMessage("m1"));

		testling.handleAckReceived(3);

		assertEquals(3, (ackedStanzas.size()));
		assertEquals(("iq1"), ackedStanzas.get(0).getID());
		assertEquals(("p1"), ackedStanzas.get(1).getID());
		assertEquals(("m1"), ackedStanzas.get(2).getID());
	}

	@Test
	public void testHandleAckReceived_MultipleAcks() {
		StanzaAckRequester testling = createRequester();
		testling.handleStanzaSent(createMessage("m1"));
		testling.handleAckReceived(1);

		testling.handleStanzaSent(createMessage("m2"));
		testling.handleStanzaSent(createMessage("m3"));
		testling.handleAckReceived(3);

		assertEquals(3, (ackedStanzas.size()));
		assertEquals(("m1"), ackedStanzas.get(0).getID());
		assertEquals(("m2"), ackedStanzas.get(1).getID());
		assertEquals(("m3"), ackedStanzas.get(2).getID());
	}

	// Handle stanza ack count wrapping, as per the XEP
	@Test
	public void testHandleAckReceived_WrapAround() {
		StanzaAckRequester testling = createRequester();
		testling.lastHandledStanzasCount = Long.parseLong("4294967295");
		testling.handleStanzaSent(createMessage("m1"));
		testling.handleStanzaSent(createMessage("m2"));

		testling.handleAckReceived(1);

		assertEquals(2, (ackedStanzas.size()));
		assertEquals(("m1"), ackedStanzas.get(0).getID());
		assertEquals(("m2"), ackedStanzas.get(1).getID());
	}
}
