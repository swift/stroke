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

package com.isode.stroke.disco;

import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.crypto.JavaCryptoProvider;
import com.isode.stroke.elements.DiscoInfo;
import com.isode.stroke.elements.Presence;
import com.isode.stroke.elements.CapsInfo;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.client.DummyStanzaChannel;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.disco.CapsInfoGenerator;
import com.isode.stroke.disco.CapsMemoryStorage;
import com.isode.stroke.disco.CapsManager;
import com.isode.stroke.jid.JID;
import java.util.Vector;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Before;

public class CapsManagerTest {

	private DummyStanzaChannel stanzaChannel;
	private IQRouter iqRouter;
	private CapsStorage storage;
	private Vector<JID> changes = new Vector<JID>();
	private JID user1;
	private DiscoInfo discoInfo1;
	private CapsInfo capsInfo1;
	private CapsInfo capsInfo1alt;
	private JID user2;
	private DiscoInfo discoInfo2;
	private CapsInfo capsInfo2;
	private CapsInfo legacyCapsInfo;
	private JID user3;
	private CryptoProvider crypto;

	@Before
	public void setUp() {
		crypto = new JavaCryptoProvider();
		stanzaChannel = new DummyStanzaChannel();
		iqRouter = new IQRouter(stanzaChannel);
		storage = new CapsMemoryStorage();
		user1 = new JID("user1@bar.com/bla");
		discoInfo1 = new DiscoInfo();
		discoInfo1.addFeature("http://swift.im/feature1");
		capsInfo1 = new CapsInfoGenerator("http://node1.im", crypto).generateCapsInfo(discoInfo1);
		capsInfo1alt = new CapsInfoGenerator("http://node2.im", crypto).generateCapsInfo(discoInfo1);
		user2 = new JID("user2@foo.com/baz");
		discoInfo2 = new DiscoInfo();
		discoInfo2.addFeature("http://swift.im/feature2");
		capsInfo2 = new CapsInfoGenerator("http://node2.im", crypto).generateCapsInfo(discoInfo2);
		user3 = new JID("user3@foo.com/baz");
		legacyCapsInfo = new CapsInfo("http://swift.im", "ver1", "");
	}

	private CapsManager createManager() {
		CapsManager manager = new CapsManager(storage, stanzaChannel, iqRouter, crypto);
		manager.setWarnOnInvalidHash(false);
		//manager.onCapsChanged.connect(boost::bind(&CapsManagerTest::handleCapsChanged, this, _1));
		return manager;
	}

	private void handleCapsChanged(JID jid) {
		changes.add(jid);
	}

	private void sendPresenceWithCaps(JID jid, CapsInfo caps) {
		Presence presence = new Presence();
		presence.setFrom(jid);
		presence.addPayload(caps);
		stanzaChannel.onPresenceReceived.emit(presence);
	}

	private void sendDiscoInfoResult(DiscoInfo discoInfo) {
		stanzaChannel.onIQReceived.emit(IQ.createResult(new JID("baz@fum.com/dum"), stanzaChannel.sentStanzas.get(0).getTo(), stanzaChannel.sentStanzas.get(0).getID(), discoInfo));
	}

	@Test
	public void testReceiveNewHashRequestsDisco() {
		CapsManager testling = createManager();
		sendPresenceWithCaps(user1, capsInfo1);

		assertTrue(stanzaChannel.isRequestAtIndex(0, user1, IQ.Type.Get, new DiscoInfo()));
		DiscoInfo discoInfo = stanzaChannel.sentStanzas.get(0).getPayload(new DiscoInfo());
		assertNotNull(discoInfo);
		assertEquals("http://node1.im#" + capsInfo1.getVersion(), discoInfo.getNode());
	}

	@Test
	public void testReceiveSameHashDoesNotRequestDisco() {
		CapsManager testling = createManager();
		sendPresenceWithCaps(user1, capsInfo1);
		stanzaChannel.sentStanzas.clear();
		sendPresenceWithCaps(user1, capsInfo1);

		assertEquals(0, stanzaChannel.sentStanzas.size());
	}

	@Test
	public void testReceiveLegacyCapsDoesNotRequestDisco() {
		CapsManager testling = createManager();
		sendPresenceWithCaps(user1, legacyCapsInfo);

		assertEquals(0, stanzaChannel.sentStanzas.size());
	}

	@Test
	public void testReceiveSameHashAfterSuccesfulDiscoDoesNotRequestDisco() {
		CapsManager testling = createManager();
		sendPresenceWithCaps(user1, capsInfo1);
		sendDiscoInfoResult(discoInfo1);

		stanzaChannel.sentStanzas.clear();
		sendPresenceWithCaps(user1, capsInfo1);

		assertEquals(0, stanzaChannel.sentStanzas.size());
	}

	@Test
	public void testReceiveSameHashFromSameUserAfterFailedDiscoDoesNotRequestDisco() {
		CapsManager testling = createManager();
		sendPresenceWithCaps(user1, capsInfo1);
		stanzaChannel.onIQReceived.emit(IQ.createError(new JID("baz@fum.com/foo"), stanzaChannel.sentStanzas.get(0).getID()));

		stanzaChannel.sentStanzas.clear();
		sendPresenceWithCaps(user1, capsInfo1);

		assertEquals(0, stanzaChannel.sentStanzas.size());
	}

	@Test
	public void testReceiveSameHashFromSameUserAfterIncorrectVerificationDoesNotRequestDisco() {
		CapsManager testling = createManager();
		sendPresenceWithCaps(user1, capsInfo1);
		sendDiscoInfoResult(discoInfo2);

		stanzaChannel.sentStanzas.clear();
		sendPresenceWithCaps(user1, capsInfo1);

		assertEquals(0, stanzaChannel.sentStanzas.size());
	}

	@Test
	public void testReceiveSameHashFromDifferentUserAfterFailedDiscoRequestsDisco() {
		CapsManager testling = createManager();
		sendPresenceWithCaps(user1, capsInfo1);
		stanzaChannel.onIQReceived.emit(IQ.createError(new JID("baz@fum.com/foo"), stanzaChannel.sentStanzas.get(0).getTo(), stanzaChannel.sentStanzas.get(0).getID()));

		stanzaChannel.sentStanzas.clear();
		sendPresenceWithCaps(user2, capsInfo1);
		assertTrue(stanzaChannel.isRequestAtIndex(0, user2, IQ.Type.Get, new DiscoInfo()));
	}

	@Test
	public void testReceiveSameHashFromDifferentUserAfterIncorrectVerificationRequestsDisco() {
		CapsManager testling = createManager();
		sendPresenceWithCaps(user1, capsInfo1);
		sendDiscoInfoResult(discoInfo2);

		stanzaChannel.sentStanzas.clear();
		sendPresenceWithCaps(user2, capsInfo1);
		assertTrue(stanzaChannel.isRequestAtIndex(0, user2, IQ.Type.Get, new DiscoInfo()));
	}

	@Test
	public void testReceiveDifferentHashFromSameUserAfterFailedDiscoDoesNotRequestDisco() {
		CapsManager testling = createManager();
		sendPresenceWithCaps(user1, capsInfo1);
		stanzaChannel.onIQReceived.emit(IQ.createError(new JID("baz@fum.com/foo"), stanzaChannel.sentStanzas.get(0).getID()));

		stanzaChannel.sentStanzas.clear();
		sendPresenceWithCaps(user1, capsInfo2);

		assertTrue(stanzaChannel.isRequestAtIndex(0, user1, IQ.Type.Get, new DiscoInfo()));
	}

	@Test
	public void testReceiveSuccesfulDiscoStoresCaps() {
		CapsManager testling = createManager();
		sendPresenceWithCaps(user1, capsInfo1);
		sendDiscoInfoResult(discoInfo1);

		DiscoInfo discoInfo = storage.getDiscoInfo(capsInfo1.getVersion());
		assertNotNull(discoInfo);
		assertTrue(discoInfo.hasFeature("http://swift.im/feature1"));
	}

	@Test
	public void testReceiveIncorrectVerificationDiscoDoesNotStoreCaps() {
		CapsManager testling = createManager();
		sendPresenceWithCaps(user1, capsInfo1);
		sendDiscoInfoResult(discoInfo2);

		DiscoInfo discoInfo = storage.getDiscoInfo(capsInfo1.getVersion());
		assertNull(discoInfo);
	}

	@Test
	public void testReceiveFailingDiscoFallsBack() {
		CapsManager testling = createManager();
		sendPresenceWithCaps(user1, capsInfo1);
		sendPresenceWithCaps(user2, capsInfo1alt);
		stanzaChannel.onIQReceived.emit(IQ.createError(new JID("baz@fum.com/foo"), stanzaChannel.sentStanzas.get(0).getTo(), stanzaChannel.sentStanzas.get(0).getID()));

		assertTrue(stanzaChannel.isRequestAtIndex(1, user2, IQ.Type.Get, new DiscoInfo()));
		DiscoInfo discoInfo = stanzaChannel.sentStanzas.get(1).getPayload(new DiscoInfo());
		assertNotNull(discoInfo);
		assertEquals("http://node2.im#" + capsInfo1alt.getVersion(), discoInfo.getNode());
	}

	@Test
	public void testReceiveNoDiscoFallsBack() {
		CapsManager testling = createManager();
		sendPresenceWithCaps(user1, capsInfo1);
		sendPresenceWithCaps(user2, capsInfo1alt);
		stanzaChannel.onIQReceived.emit(IQ.createResult(new JID("baz@fum.com/dum"), stanzaChannel.sentStanzas.get(0).getTo(), stanzaChannel.sentStanzas.get(0).getID(), new DiscoInfo()));

		assertTrue(stanzaChannel.isRequestAtIndex(1, user2, IQ.Type.Get, new DiscoInfo()));
		DiscoInfo discoInfo = stanzaChannel.sentStanzas.get(1).getPayload(new DiscoInfo());
		assertNotNull(discoInfo);
		assertEquals("http://node2.im#" + capsInfo1alt.getVersion(), discoInfo.getNode());
	}

	@Test
	public void testReceiveFailingFallbackDiscoFallsBack() {
		CapsManager testling = createManager();
		sendPresenceWithCaps(user1, capsInfo1);
		sendPresenceWithCaps(user2, capsInfo1alt);
		sendPresenceWithCaps(user3, capsInfo1);
		stanzaChannel.onIQReceived.emit(IQ.createError(new JID("baz@fum.com/foo"), stanzaChannel.sentStanzas.get(0).getTo(), stanzaChannel.sentStanzas.get(0).getID()));
		stanzaChannel.onIQReceived.emit(IQ.createError(new JID("baz@fum.com/foo"), stanzaChannel.sentStanzas.get(1).getTo(), stanzaChannel.sentStanzas.get(1).getID()));

		assertTrue(stanzaChannel.isRequestAtIndex(2, user3, IQ.Type.Get, new DiscoInfo()));
	}

	@Test
	public void testReceiveSameHashFromFailingUserAfterReconnectRequestsDisco() {
		CapsManager testling = createManager();
		sendPresenceWithCaps(user1, capsInfo1);
		stanzaChannel.onIQReceived.emit(IQ.createError(new JID("baz@fum.com/foo"), stanzaChannel.sentStanzas.get(0).getTo(), stanzaChannel.sentStanzas.get(0).getID()));
		stanzaChannel.setAvailable(false);
		stanzaChannel.setAvailable(true);
		stanzaChannel.sentStanzas.clear();

		sendPresenceWithCaps(user1, capsInfo1);

		assertTrue(stanzaChannel.isRequestAtIndex(0, user1, IQ.Type.Get, new DiscoInfo()));
	}

	@Test
	public void testReconnectResetsFallback() {
		CapsManager testling = createManager();
		sendPresenceWithCaps(user1, capsInfo1);
		sendPresenceWithCaps(user2, capsInfo1alt);
		stanzaChannel.setAvailable(false);
		stanzaChannel.setAvailable(true);
		stanzaChannel.sentStanzas.clear();
		sendPresenceWithCaps(user1, capsInfo1);
		stanzaChannel.onIQReceived.emit(IQ.createError(new JID("baz@fum.com/foo"), stanzaChannel.sentStanzas.get(0).getTo(), stanzaChannel.sentStanzas.get(0).getID()));

		assertEquals(1, stanzaChannel.sentStanzas.size());
	}

	@Test
	public void testReconnectResetsRequests() {
		CapsManager testling = createManager();
		sendPresenceWithCaps(user1, capsInfo1);
		stanzaChannel.sentStanzas.clear();
		stanzaChannel.setAvailable(false);
		stanzaChannel.setAvailable(true);
		sendPresenceWithCaps(user1, capsInfo1);

		assertTrue(stanzaChannel.isRequestAtIndex(0, user1, IQ.Type.Get, new DiscoInfo()));
	}
}