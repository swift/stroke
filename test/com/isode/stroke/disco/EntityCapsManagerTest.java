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
import com.isode.stroke.client.DummyStanzaChannel;
import com.isode.stroke.elements.CapsInfo;
import com.isode.stroke.elements.DiscoInfo;
import com.isode.stroke.elements.Presence;
import com.isode.stroke.disco.EntityCapsManager;
import com.isode.stroke.disco.CapsInfoGenerator;
import com.isode.stroke.disco.CapsProvider;
import com.isode.stroke.signals.Signal1;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.jid.JID;
import java.util.Vector;
import java.util.Map;
import java.util.HashMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Before;

public class EntityCapsManagerTest {

	private DummyStanzaChannel stanzaChannel;
	private DummyCapsProvider capsProvider;
	private JID user1;
	private DiscoInfo discoInfo1;
	private CapsInfo capsInfo1;
	private CapsInfo capsInfo1alt;
	private JID user2;
	private DiscoInfo discoInfo2;
	private CapsInfo capsInfo2;
	private CapsInfo legacyCapsInfo;
	private JID user3;
	private Vector<JID> changes = new Vector<JID>();
	private CryptoProvider crypto;
	private SignalConnection onCapsChangedConnection;

	private class DummyCapsProvider extends CapsProvider {
		public DiscoInfo getCaps(String hash) {
			if(caps.containsKey(hash)) {
				return caps.get(hash);
			} else {
				return null;
			}
		}

		public Map<String, DiscoInfo> caps = new HashMap<String, DiscoInfo>();
	}

	@Before
	public void setUp() {
		crypto = new JavaCryptoProvider();

		stanzaChannel = new DummyStanzaChannel();
		capsProvider = new DummyCapsProvider();

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

	private EntityCapsManager createManager() {
		EntityCapsManager manager = new EntityCapsManager(capsProvider, stanzaChannel);
		onCapsChangedConnection = manager.onCapsChanged.connect(new Slot1<JID>() {

			@Override
			public void call(JID j1) {
				handleCapsChanged(j1);
			}
		});
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

	private void sendUnavailablePresence(JID jid) {
		Presence presence = new Presence();
		presence.setFrom(jid);
		presence.setType(Presence.Type.Unavailable);
		stanzaChannel.onPresenceReceived.emit(presence);
	}

	@Test
	public void testReceiveKnownHash() {
		EntityCapsManager testling = createManager();
		capsProvider.caps.put(capsInfo1.getVersion(), discoInfo1);
		sendPresenceWithCaps(user1, capsInfo1);

		assertEquals(1, changes.size());
		assertEquals(user1, changes.get(0));
		assertEquals(discoInfo1, testling.getCaps(user1));
	}

	@Test
	public void testReceiveKnownHashTwiceDoesNotTriggerChange() {
		EntityCapsManager testling = createManager();
		capsProvider.caps.put(capsInfo1.getVersion(), discoInfo1);
		sendPresenceWithCaps(user1, capsInfo1);
		changes.clear();

		sendPresenceWithCaps(user1, capsInfo1);

		assertEquals(0, changes.size());
	}

	@Test
	public void testReceiveUnknownHashDoesNotTriggerChange() {
		EntityCapsManager testling = createManager();
		sendPresenceWithCaps(user1, capsInfo1);

		assertEquals(0, changes.size());
	}

	@Test
	public void testHashAvailable() {
		EntityCapsManager testling = createManager();
		sendPresenceWithCaps(user1, capsInfo1);

		capsProvider.caps.put(capsInfo1.getVersion(), discoInfo1);
		capsProvider.onCapsAvailable.emit(capsInfo1.getVersion());

		assertEquals(1, changes.size());
		assertEquals(user1, changes.get(0));
		assertEquals(discoInfo1, testling.getCaps(user1));
	}

	@Test
	public void testReceiveUnknownHashAfterKnownHashTriggersChangeAndClearsCaps() {
		EntityCapsManager testling = createManager();
		capsProvider.caps.put(capsInfo1.getVersion(), discoInfo1);
		sendPresenceWithCaps(user1, capsInfo1);
		changes.clear();
		sendPresenceWithCaps(user1, capsInfo2);

		assertEquals(1, changes.size());
		assertEquals(user1, changes.get(0));
		assertNull(testling.getCaps(user1));
	}

	@Test
	public void testReceiveUnavailablePresenceAfterKnownHashTriggersChangeAndClearsCaps() {
		EntityCapsManager testling = createManager();
		capsProvider.caps.put(capsInfo1.getVersion(), discoInfo1);
		sendPresenceWithCaps(user1, capsInfo1);
		changes.clear();
		sendUnavailablePresence(user1);

		assertEquals(1, changes.size());
		assertEquals(user1, changes.get(0));
		assertNull(testling.getCaps(user1));
	}

	@Test
	public void testReconnectTriggersChangeAndClearsCaps() {
		EntityCapsManager testling = createManager();
		capsProvider.caps.put(capsInfo1.getVersion(), discoInfo1);
		capsProvider.caps.put(capsInfo2.getVersion(), discoInfo2);
		sendPresenceWithCaps(user1, capsInfo1);
		sendPresenceWithCaps(user2, capsInfo2);
		changes.clear();
		stanzaChannel.setAvailable(false);
		stanzaChannel.setAvailable(true);

		assertEquals(2, changes.size());
		assertEquals(user1, changes.get(0));
		assertNull(testling.getCaps(user1));
		assertEquals(user2, changes.get(1));
		assertNull(testling.getCaps(user2));
	}
}