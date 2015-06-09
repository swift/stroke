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

package com.isode.stroke.avatars;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import java.util.Vector;
import com.isode.stroke.elements.VCardUpdate;
import com.isode.stroke.elements.Presence;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.elements.VCard;
import com.isode.stroke.avatars.VCardUpdateAvatarManager;
import com.isode.stroke.avatars.VCardAvatarManager;
import com.isode.stroke.avatars.AvatarMemoryStorage;
import com.isode.stroke.vcards.VCardMemoryStorage;
import com.isode.stroke.vcards.VCardManager;
import com.isode.stroke.muc.MUCRegistry;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.client.DummyStanzaChannel;
import com.isode.stroke.crypto.JavaCryptoProvider;
import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.stringcodecs.Hexify;
import com.isode.stroke.jid.JID;
import com.isode.stroke.base.ByteArray;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot1;

public class VCardUpdateAvatarManagerTest {

	private class DummyMUCRegistry extends MUCRegistry {

		private Vector<JID> mucs_ = new Vector<JID>();

		public boolean isMUC(JID jid) {
			return mucs_.contains(jid);
		}
	}

	private JID ownJID;
	private DummyStanzaChannel stanzaChannel;
	private IQRouter iqRouter;
	private DummyMUCRegistry mucRegistry;
	private AvatarMemoryStorage avatarStorage;
	private VCardManager vcardManager;
	private VCardMemoryStorage vcardStorage;
	private ByteArray avatar1;
	private String avatar1Hash;
	private Vector<JID> changes;
	private JID user1;
	private JID user2;
	private CryptoProvider crypto;
	private SignalConnection onAvatarChangedConnection;

	@Before
	public void setUp() {
		crypto = new JavaCryptoProvider();
		ownJID = new JID("foo@fum.com/bum");
		stanzaChannel = new DummyStanzaChannel();
		stanzaChannel.setAvailable(true);
		iqRouter = new IQRouter(stanzaChannel);
		mucRegistry = new DummyMUCRegistry();
		avatarStorage = new AvatarMemoryStorage();
		vcardStorage = new VCardMemoryStorage(crypto);
		vcardManager = new VCardManager(ownJID, iqRouter, vcardStorage);
		avatar1 = new ByteArray("abcdefg");
		avatar1Hash = Hexify.hexify(crypto.getSHA1Hash(avatar1));
		changes = new Vector<JID>();
		user1 = new JID("user1@bar.com/bla");
		user2 = new JID("user2@foo.com/baz");
	}

	private VCardUpdateAvatarManager createManager() {
		VCardUpdateAvatarManager result = new VCardUpdateAvatarManager(vcardManager, stanzaChannel, avatarStorage, crypto, mucRegistry);
		onAvatarChangedConnection = result.onAvatarChanged.connect(new Slot1<JID>() {

			public void call(JID j1) {
				handleAvatarChanged(j1);
			}
		});
		return result;
	}

	private Presence createPresenceWithPhotoHash(JID jid, String hash) {
		Presence presence = new Presence();
		presence.setFrom(jid);
		presence.addPayload(new VCardUpdate(hash));
		return presence;
	}

	private IQ createVCardResult(ByteArray avatar) {
		VCard vcard = new VCard();
		if (!avatar.isEmpty()) {
			vcard.setPhoto(avatar);
		}
		return IQ.createResult(new JID("baz@fum.com"), stanzaChannel.sentStanzas.get(0).getTo(), stanzaChannel.sentStanzas.get(0).getID(), vcard);
	}

	private void handleAvatarChanged(JID jid) {
		changes.add(jid);
	}

	@Test
	public void testUpdate_NewHashNewVCardRequestsVCard() {
		VCardUpdateAvatarManager testling = createManager();
		stanzaChannel.onPresenceReceived.emit(createPresenceWithPhotoHash(user1, avatar1Hash));

		assertEquals(1, stanzaChannel.sentStanzas.size());
		assertTrue(stanzaChannel.isRequestAtIndex(0, user1.toBare(), IQ.Type.Get, new VCard()));
	}

	@Test
	public void testUpdate_NewHashStoresAvatarAndEmitsNotificationOnVCardReceive() {
		VCardUpdateAvatarManager testling = createManager();
		stanzaChannel.onPresenceReceived.emit(createPresenceWithPhotoHash(user1, avatar1Hash));
		stanzaChannel.onIQReceived.emit(createVCardResult(avatar1));

		assertEquals(1, changes.size());
		assertEquals(user1.toBare(), changes.get(0));
		String hash = testling.getAvatarHash(user1.toBare());
		assertNotNull(hash);
		assertEquals(avatar1Hash, hash);
		assertTrue(avatarStorage.hasAvatar(avatar1Hash));
		assertEquals(avatar1, avatarStorage.getAvatar(avatar1Hash));
	}

	@Test
	public void testUpdate_KnownHash() {
		VCardUpdateAvatarManager testling = createManager();
		stanzaChannel.onPresenceReceived.emit(createPresenceWithPhotoHash(user1, avatar1Hash));
		stanzaChannel.onIQReceived.emit(createVCardResult(avatar1));
		changes.clear();
		stanzaChannel.sentStanzas.clear();

		stanzaChannel.onPresenceReceived.emit(createPresenceWithPhotoHash(user1, avatar1Hash));

		assertEquals(0, stanzaChannel.sentStanzas.size());
		assertEquals(0, changes.size());
	}

	@Test
	public void testUpdate_KnownHashFromDifferentUserDoesNotRequestVCardButTriggersNotification() {
		VCardUpdateAvatarManager testling = createManager();
		stanzaChannel.onPresenceReceived.emit(createPresenceWithPhotoHash(user1, avatar1Hash));
		stanzaChannel.onIQReceived.emit(createVCardResult(avatar1));
		changes.clear();
		stanzaChannel.sentStanzas.clear();

		stanzaChannel.onPresenceReceived.emit(createPresenceWithPhotoHash(user2, avatar1Hash));
		assertEquals(0, stanzaChannel.sentStanzas.size());
		assertEquals(1, changes.size());
		assertEquals(user2.toBare(), changes.get(0));
		String hash = testling.getAvatarHash(user2.toBare());
		assertNotNull(hash);
		assertEquals(avatar1Hash, hash);
	}

	
	@Test
	public void testVCardWithEmptyPhoto() {
		VCardUpdateAvatarManager testling = createManager();
		vcardManager.requestVCard(new JID("foo@bar.com"));
		stanzaChannel.onIQReceived.emit(createVCardResult(new ByteArray()));

		assertTrue(!avatarStorage.hasAvatar(Hexify.hexify(crypto.getSHA1Hash(new ByteArray()))));
		String hash = testling.getAvatarHash(new JID("foo@bar.com"));
		assertNotNull(hash);
		assertEquals("", hash);
	}

	@Test
	public void testStanzaChannelReset_ClearsHash() {
		VCardUpdateAvatarManager testling = createManager();
		stanzaChannel.onPresenceReceived.emit(createPresenceWithPhotoHash(user1, avatar1Hash));
		stanzaChannel.onIQReceived.emit(createVCardResult(avatar1));
		changes.clear();
		stanzaChannel.sentStanzas.clear();

		stanzaChannel.setAvailable(false);
		stanzaChannel.setAvailable(true);

		assertEquals(1, changes.size());
		assertEquals(user1.toBare(), changes.get(0));
		String hash = testling.getAvatarHash(user1.toBare());
		assertNull(hash);
	}

	@Test
	public void testStanzaChannelReset_ReceiveHashAfterResetUpdatesHash() {
		VCardUpdateAvatarManager testling = createManager();
		stanzaChannel.onPresenceReceived.emit(createPresenceWithPhotoHash(user1, avatar1Hash));
		stanzaChannel.onIQReceived.emit(createVCardResult(avatar1));
		changes.clear();
		stanzaChannel.sentStanzas.clear();

		stanzaChannel.setAvailable(false);
		stanzaChannel.setAvailable(true);
		stanzaChannel.onPresenceReceived.emit(createPresenceWithPhotoHash(user1, avatar1Hash));

		assertEquals(2, changes.size());
		assertEquals(user1.toBare(), changes.get(1));
		String hash = testling.getAvatarHash(user1.toBare());
		assertNotNull(hash);
		assertEquals(avatar1Hash, hash);
	}
}