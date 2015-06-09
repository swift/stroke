/*
 * Copyright (c) 2014 Isode Limited.
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
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

public class AvatarManagerImplTest {

	private JID ownerJID;
	private DummyStanzaChannel stanzaChannel;
	private IQRouter iqRouter;
	private CryptoProvider crypto;
	private VCardMemoryStorage vcardStorage;
	private VCardManager vcardManager;
	private AvatarMemoryStorage avatarStorage;
	private DummyMUCRegistry mucRegistry;
	private AvatarManagerImpl avatarManager;

	public class DummyMUCRegistry extends MUCRegistry {

		public boolean isMUC(JID jid) {
			return mucs_.contains(jid);
		}

		public Vector<JID> mucs_ = new Vector<JID>();
	}

	@Before
	public void setUp() {
		ownerJID = new JID("owner@domain.com/theowner");
		stanzaChannel = new DummyStanzaChannel();
		iqRouter = new IQRouter(stanzaChannel);
		crypto = new JavaCryptoProvider();
		vcardStorage = new VCardMemoryStorage(crypto);
		vcardManager = new VCardManager(ownerJID, iqRouter, vcardStorage);
		avatarStorage = new AvatarMemoryStorage();
		mucRegistry = new DummyMUCRegistry();
		avatarManager = new AvatarManagerImpl(vcardManager, stanzaChannel, avatarStorage, crypto, mucRegistry);
	}

	@Test
	public void testGetSetAvatar() {
		/* initially we have no knowledge of the user or their avatar */
		JID personJID = new JID("person@domain.com/theperson");
		ByteArray avatar = avatarManager.getAvatar(personJID.toBare());
		assertTrue(avatar.getSize() == 0);

		/* notify the 'owner' JID that our avatar has changed */

		ByteArray fullAvatar = new ByteArray("abcdefg");
		VCardUpdate vcardUpdate = new VCardUpdate();
		vcardUpdate.setPhotoHash(Hexify.hexify(crypto.getSHA1Hash(fullAvatar)));
		Presence presence = new Presence();
		presence.setTo(ownerJID);
		presence.setFrom(personJID);
		presence.setType(Presence.Type.Available);
		presence.addPayload(vcardUpdate);
		stanzaChannel.onPresenceReceived.emit(presence);

		/* reply to the avatar request with our new avatar */

		assertEquals(1, stanzaChannel.sentStanzas.size());
		IQ request = (IQ)(stanzaChannel.sentStanzas.get(0));
		stanzaChannel.sentStanzas.remove(stanzaChannel.sentStanzas.lastElement());
		assertNotNull(request);
		VCard vcard = request.getPayload(new VCard());
		assertNotNull(vcard);

		IQ reply = new IQ(IQ.Type.Result);
		reply.setTo(request.getFrom());
		reply.setFrom(request.getTo());
		reply.setID(request.getID());
		vcard.setPhoto(fullAvatar);
		reply.addPayload(vcard);
		stanzaChannel.onIQReceived.emit(reply);

		/* check hash through avatarManager that it received the correct photo */

		ByteArray reportedAvatar = avatarManager.getAvatar(personJID.toBare());
		assertEquals(fullAvatar.toString(), reportedAvatar.toString());

		/* send new presence to notify of blank avatar */

		vcardUpdate = new VCardUpdate();
		presence = new Presence();
		presence.setTo(ownerJID);
		presence.setFrom(personJID);
		presence.setType(Presence.Type.Available);
		presence.addPayload(vcardUpdate);
		stanzaChannel.onPresenceReceived.emit(presence);

		/* reply to the avatar request with our EMPTY avatar */

		assertEquals(1, stanzaChannel.sentStanzas.size());
		request = (IQ)(stanzaChannel.sentStanzas.get(0));
		stanzaChannel.sentStanzas.remove(stanzaChannel.sentStanzas.lastElement());
		assertNotNull(request);
		vcard = request.getPayload(new VCard());
		assertNotNull(vcard);

		ByteArray blankAvatar = new ByteArray("");
		reply = new IQ(IQ.Type.Result);
		reply.setTo(request.getFrom());
		reply.setFrom(request.getTo());
		reply.setID(request.getID());
		vcard.setPhoto(blankAvatar);
		reply.addPayload(vcard);
		stanzaChannel.onIQReceived.emit(reply);

		/* check hash through avatarManager that it received the correct photo */

		reportedAvatar = avatarManager.getAvatar(personJID.toBare());
		assertEquals(blankAvatar.toString(), reportedAvatar.toString());
	}
}