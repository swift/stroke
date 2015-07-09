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

package com.isode.stroke.vcards;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Before;
import com.isode.stroke.vcards.VCardManager;
import com.isode.stroke.vcards.VCardMemoryStorage;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.client.DummyStanzaChannel;
import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.crypto.JavaCryptoProvider;
import com.isode.stroke.jid.JID;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.elements.VCard;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.signals.Slot2;
import java.util.Vector;

public class VCardManagerTest {

	private JID ownJID;
	private DummyStanzaChannel stanzaChannel;
	private IQRouter iqRouter;
	private VCardMemoryStorage vcardStorage;
	private Vector<Pair> changes = new Vector<Pair>();
	private Vector<VCard> ownChanges = new Vector<VCard>();
	private CryptoProvider crypto;

	private class Pair {
		public JID jid;
		public VCard vcard;

		Pair(JID j, VCard v) {jid = j; vcard = v;}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Pair)) return false;
			Pair o1 = (Pair) o;
			return jid.equals(o1.jid) && vcard.equals(o1.vcard);
		}

		@Override public int hashCode() {return jid.hashCode() * 5 + vcard.hashCode();}
	}

	@Before
	public void setUp() {
		ownJID = new JID("baz@fum.com/dum");
		crypto = new JavaCryptoProvider();
		stanzaChannel = new DummyStanzaChannel();
		iqRouter = new IQRouter(stanzaChannel);
		vcardStorage = new VCardMemoryStorage(crypto);
	}

	private VCardManager createManager() {
		VCardManager manager = new VCardManager(ownJID, iqRouter, vcardStorage);
		manager.onVCardChanged.connect(new Slot2<JID, VCard>() {
			@Override
			public void call(JID j1, VCard v1) {
				handleVCardChanged(j1, v1);
			}
		});
		manager.onOwnVCardChanged.connect(new Slot1<VCard>() {
			@Override
			public void call(VCard v1) {
				handleOwnVCardChanged(v1);
			}
		});
		return manager;
	}

	private void handleVCardChanged(final JID jid, VCard vcard) {
		changes.add(new Pair(jid, vcard));
	}

	private void handleOwnVCardChanged(VCard vcard) {
		ownChanges.add(vcard);
	}

	private IQ createVCardResult() {
		VCard vcard = new VCard();
		vcard.setFullName("Foo Bar");
		return IQ.createResult(new JID("baz@fum.com/dum"), stanzaChannel.sentStanzas.get(0).getTo(), stanzaChannel.sentStanzas.get(0).getID(), vcard);
	}

	private IQ createOwnVCardResult() {
		VCard vcard = new VCard();
		vcard.setFullName("Myself");
		return IQ.createResult(new JID(), stanzaChannel.sentStanzas.get(0).getTo(), stanzaChannel.sentStanzas.get(0).getID(), vcard);
	}

	private IQ createSetVCardResult() {
		return IQ.createResult(new JID("baz@fum.com/dum"), stanzaChannel.sentStanzas.get(0).getTo(), stanzaChannel.sentStanzas.get(0).getID(), null);
	}

	@Test
	public void testGet_NewVCardRequestsVCard() {
		VCardManager testling = createManager();
		VCard result = testling.getVCardAndRequestWhenNeeded(new JID("foo@bar.com/baz"));

		assertNull(result);
		assertEquals(1, (stanzaChannel.sentStanzas.size()));
		assertTrue(stanzaChannel.isRequestAtIndex(0, new JID("foo@bar.com/baz"), IQ.Type.Get, new VCard()));
	}

	@Test
	public void testGet_ExistingVCard() {
		VCardManager testling = createManager();
		VCard vcard = new VCard();
		vcard.setFullName("Foo Bar");
		vcardStorage.setVCard(new JID("foo@bar.com/baz"), vcard);

		VCard result = testling.getVCardAndRequestWhenNeeded(new JID("foo@bar.com/baz"));

		assertEquals("Foo Bar", result.getFullName());
		assertEquals(0, (stanzaChannel.sentStanzas.size()));
	}

	@Test
	public void testRequest_RequestsVCard() {
		VCardManager testling = createManager();
		testling.requestVCard(new JID("foo@bar.com/baz"));

		assertEquals(1, (stanzaChannel.sentStanzas.size()));
		assertTrue(stanzaChannel.isRequestAtIndex(0, new JID("foo@bar.com/baz"), IQ.Type.Get, new VCard()));
	}

	@Test
	public void testRequest_ReceiveEmitsNotification() {
		VCardManager testling = createManager();
		testling.requestVCard(new JID("foo@bar.com/baz"));
		stanzaChannel.onIQReceived.emit(createVCardResult());

		assertEquals(1, (changes.size()));
		assertEquals(new JID("foo@bar.com/baz"), changes.get(0).jid);
		assertEquals(("Foo Bar"), changes.get(0).vcard.getFullName());
		assertEquals(("Foo Bar"), vcardStorage.getVCard(new JID("foo@bar.com/baz")).getFullName());

		assertEquals(0, (ownChanges.size()));
	}

	@Test
	public void testRequest_Error() {
		VCardManager testling = createManager();
		testling.requestVCard(new JID("foo@bar.com/baz"));
		stanzaChannel.onIQReceived.emit(IQ.createError(new JID("baz@fum.com/foo"), stanzaChannel.sentStanzas.get(0).getTo(), stanzaChannel.sentStanzas.get(0).getID()));

		assertEquals(1, (changes.size()));
		assertEquals(new JID("foo@bar.com/baz"), changes.get(0).jid);
		assertEquals((""), changes.get(0).vcard.getFullName());
		assertEquals((""), vcardStorage.getVCard(new JID("foo@bar.com/baz")).getFullName());
	}

	@Test
	public void testRequest_VCardAlreadyRequested() {
		VCardManager testling = createManager();
		testling.requestVCard(new JID("foo@bar.com/baz"));
		VCard result = testling.getVCardAndRequestWhenNeeded(new JID("foo@bar.com/baz"));

		assertNull(result);
		assertEquals(1, (stanzaChannel.sentStanzas.size()));
	}

	@Test
	public void testRequest_AfterPreviousRequest() {
		VCardManager testling = createManager();
		testling.requestVCard(new JID("foo@bar.com/baz"));
		stanzaChannel.onIQReceived.emit(createVCardResult());
		testling.requestVCard(new JID("foo@bar.com/baz"));

		assertEquals(2, (stanzaChannel.sentStanzas.size()));
		assertTrue(stanzaChannel.isRequestAtIndex(1, new JID("foo@bar.com/baz"), IQ.Type.Get, new VCard()));
	}

	@Test
	public void testRequestOwnVCard() {
		VCardManager testling = createManager();
		testling.requestVCard(ownJID);
		stanzaChannel.onIQReceived.emit(createOwnVCardResult());

		assertEquals(1, (stanzaChannel.sentStanzas.size()));
		assertTrue(stanzaChannel.isRequestAtIndex(0, new JID(), IQ.Type.Get, new VCard()));
		assertEquals(1, (changes.size()));
		assertEquals(ownJID.toBare(), changes.get(0).jid);
		assertEquals(("Myself"), changes.get(0).vcard.getFullName());
		assertEquals(("Myself"), vcardStorage.getVCard(ownJID.toBare()).getFullName());

		assertEquals(1, (ownChanges.size()));
		assertEquals(("Myself"), ownChanges.get(0).getFullName());
	}

	@Test
	public void testCreateSetVCardRequest() {
		VCardManager testling = createManager();
		VCard vcard = new VCard();
		vcard.setFullName("New Name");
		SetVCardRequest request = testling.createSetVCardRequest(vcard);
		request.send();

		stanzaChannel.onIQReceived.emit(createSetVCardResult());

		assertEquals(1, (changes.size()));
		assertEquals(ownJID.toBare(), changes.get(0).jid);
		assertEquals(("New Name"), changes.get(0).vcard.getFullName());
	}

	@Test
	public void testCreateSetVCardRequest_Error() {
		VCardManager testling = createManager();
		VCard vcard = new VCard();
		vcard.setFullName("New Name");
		SetVCardRequest request = testling.createSetVCardRequest(vcard);
		request.send();

		stanzaChannel.onIQReceived.emit(IQ.createError(new JID("baz@fum.com/foo"), stanzaChannel.sentStanzas.get(0).getID()));

		assertEquals(0, (changes.size()));
	}
}