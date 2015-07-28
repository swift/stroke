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

public class StanzaAckResponderTest {

	private Vector<Long> acks = new Vector<Long>();

	private Message createMessage(final String id) {
		Message result = new Message();
		result.setID(id);
		return result;
	}

	private StanzaAckResponder createResponder() {
		StanzaAckResponder responder = new StanzaAckResponder();
		responder.onAck.connect(new Slot1<Long>() {
			@Override
			public void call(Long l) {
				handleAck(l);
			}
		});
		return responder;
	}

	private void handleAck(Long h) {
		acks.add(h);
	}

	@Test
	public void testHandleAckRequestReceived_AcksStanza() {
		StanzaAckResponder testling = createResponder();
		testling.handleStanzaReceived();

		testling.handleAckRequestReceived();

		assertEquals(1, (acks.size()));
		assertEquals(Long.valueOf(1L), acks.get(0));
	}

	@Test
	public void testHandleAckRequestReceived_AcksMultipleStanzas() {
		StanzaAckResponder testling = createResponder();
		testling.handleStanzaReceived();
		testling.handleStanzaReceived();

		testling.handleAckRequestReceived();

		assertEquals(1, (acks.size()));
		assertEquals(Long.valueOf(2L), acks.get(0));
	}

	@Test
	public void testHandleAckRequestReceived_MultipleAcks() {
		StanzaAckResponder testling = createResponder();
		testling.handleStanzaReceived();
		testling.handleAckRequestReceived();

		testling.handleStanzaReceived();
		testling.handleAckRequestReceived();

		assertEquals(2, (acks.size()));
		assertEquals(Long.valueOf(1L), acks.get(0));
		assertEquals(Long.valueOf(2L), acks.get(1));
	}

	// Handle stanza ack count wrapping, as per the XEP
	@Test
	public void testHandleAckRequestReceived_WrapAround() {
		StanzaAckResponder testling = createResponder();
		testling.handledStanzasCount = Long.parseLong("4294967295");
		testling.handleStanzaReceived();
		testling.handleStanzaReceived();

		testling.handleAckRequestReceived();

		assertEquals(1, (acks.size()));
		assertEquals(Long.valueOf(1L), acks.get(0));
	}

}