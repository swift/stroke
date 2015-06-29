/*
 * Copyright (c) 2010-2015 Isode Limited.
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
import org.junit.Before;
import org.junit.Test;
import java.util.Vector;
import com.isode.stroke.avatars.VCardAvatarManager;
import com.isode.stroke.elements.VCard;
import com.isode.stroke.vcards.VCardMemoryStorage;
import com.isode.stroke.avatars.AvatarMemoryStorage;
import com.isode.stroke.vcards.VCardManager;
import com.isode.stroke.muc.MUCRegistry;
import com.isode.stroke.jid.JID;
import com.isode.stroke.base.ByteArray;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.client.DummyStanzaChannel;
import com.isode.stroke.crypto.JavaCryptoProvider;
import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.stringcodecs.Hexify;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.elements.IQ;

public class VCardAvatarManagerTest {

	private class DummyMUCRegistry extends MUCRegistry {

		private Vector<JID> mucs_ = new Vector<JID>();

		public boolean isMUC(JID jid) {
			if(mucs_.contains(jid))
				return true;
			else
				return false;
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
	private CryptoProvider crypto;

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
	}

	private VCardAvatarManager createManager() {
		VCardAvatarManager result = new VCardAvatarManager(vcardManager, avatarStorage, crypto, mucRegistry);
		result.onAvatarChanged.connect(new Slot1<JID>() {

			public void call(JID j1) {
				handleAvatarChanged(j1);
			}
		});
		return result;
	}

	private void storeVCardWithPhoto(JID jid, ByteArray avatar) {
		VCard vcard = new VCard();
		vcard.setPhoto(avatar);
		vcardStorage.setVCard(jid, vcard);
	}

	private void storeEmptyVCard(JID jid) {
		VCard vcard = new VCard();
		vcardStorage.setVCard(jid, vcard);
	}

	private void handleAvatarChanged(JID jid) {
		changes.add(jid);
	}

	private void sendVCardResult() {
		VCard vcard = new VCard();
		vcard.setFullName("Foo Bar");
		stanzaChannel.onIQReceived.emit(IQ.createResult(new JID("baz@fum.com/dum"), stanzaChannel.sentStanzas.get(0).getTo(), stanzaChannel.sentStanzas.get(0).getID(), vcard));
	}

	@Test
	public void testGetAvatarHashKnownAvatar() {
		VCardAvatarManager testling = createManager();
		storeVCardWithPhoto(user1.toBare(), avatar1);
		avatarStorage.addAvatar(avatar1Hash, avatar1);

		String result = testling.getAvatarHash(user1);
		assertNotNull(result);
		assertEquals(avatar1Hash, result);
	}

	@Test
	public void testGetAvatarHashEmptyAvatar() {
		VCardAvatarManager testling = createManager();
		storeEmptyVCard(user1.toBare());

		String result = testling.getAvatarHash(user1);
		assertNotNull(result);
		assertEquals("", result);
	}

	@Test
	public void testGetAvatarHashUnknownAvatarKnownVCardStoresAvatar() {
		VCardAvatarManager testling = createManager();
		storeVCardWithPhoto(user1.toBare(), avatar1);

		String result = testling.getAvatarHash(user1);
		assertNotNull(result);
		assertEquals(avatar1Hash, result);
		assertTrue(avatarStorage.hasAvatar(avatar1Hash));
		assertEquals(avatar1, avatarStorage.getAvatarBytes(avatar1Hash));
	}

	@Test
	public void testGetAvatarHashUnknownAvatarUnknownVCard() {
		VCardAvatarManager testling = createManager();

		String result = testling.getAvatarHash(user1);

		assertNotNull(result);
		assertEquals("", result);
	}

	@Test
	public void testGetAvatarHashKnownAvatarUnknownVCard() {
		VCardAvatarManager testling = createManager();
		avatarStorage.setAvatarForJID(user1, avatar1Hash);

		String result = testling.getAvatarHash(user1);

		assertNotNull(result);
		assertEquals("", result);
	}

	@Test
	public void testVCardUpdateTriggersUpdate() {
		VCardAvatarManager testling = createManager();
		vcardManager.requestVCard(user1);
		sendVCardResult();

		assertEquals(1, changes.size());
	}
}