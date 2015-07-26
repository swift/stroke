/*
 * Copyright (c) 2010-2013 Isode Limited.
 * All right reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Before;
import com.isode.stroke.elements.VCard;
import com.isode.stroke.elements.RosterItemPayload;
import com.isode.stroke.client.StanzaChannel;
import com.isode.stroke.client.DummyStanzaChannel;
import com.isode.stroke.client.ClientBlockListManager;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.queries.GenericRequest;
import com.isode.stroke.jid.JID;
import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.crypto.JavaCryptoProvider;
import com.isode.stroke.vcards.VCardMemoryStorage;
import com.isode.stroke.vcards.VCardManager;
import com.isode.stroke.vcards.VCardStorage;
import com.isode.stroke.roster.XMPPRosterImpl;
import com.isode.stroke.muc.MUCRegistry;
import com.isode.stroke.client.NickResolver;
import java.util.Vector;
import java.util.Collection;

public class NickResolverTest {

	private Collection<String> groups_ = new Vector<String>();
	private XMPPRosterImpl xmppRoster_;
	private VCardStorage vCardStorage_;
	private IQRouter iqRouter_;
	private DummyStanzaChannel stanzaChannel_;
	private VCardManager vCardManager_;
	private MUCRegistry registry_;
	private NickResolver resolver_;
	private JID ownJID_;
	private CryptoProvider crypto;

	public void populateOwnVCard(final String nick, final String given, final String full) {
		VCard vcard = new VCard();
		if (!nick.isEmpty()) {
			vcard.setNickname(nick);
		}
		if (!given.isEmpty()) {
			vcard.setGivenName(given);
		}
		if (!full.isEmpty()) {
			vcard.setFullName(full);
		}
		vCardManager_.requestVCard(ownJID_);
		IQ result = IQ.createResult(new JID(), stanzaChannel_.sentStanzas.get(0).getID(), vcard);
		stanzaChannel_.onIQReceived.emit(result);
	}

	@Before
	public void setUp() {
		crypto = new JavaCryptoProvider();
		ownJID_ = new JID("kev@wonderland.lit");
		xmppRoster_ = new XMPPRosterImpl();
		stanzaChannel_ = new DummyStanzaChannel();
		iqRouter_ = new IQRouter(stanzaChannel_);
		vCardStorage_ = new VCardMemoryStorage(crypto);
		vCardManager_ = new VCardManager(ownJID_, iqRouter_, vCardStorage_);
		registry_ = new MUCRegistry();
		resolver_ = new NickResolver(ownJID_, xmppRoster_, vCardManager_, registry_);
	}

	@Test
	public void testMUCNick() {
		registry_.addMUC(new JID("foo@bar"));
		JID testJID = new JID("foo@bar/baz");

		assertEquals(("baz"), resolver_.jidToNick(testJID));
	}

	@Test
	public void testMUCNoNick() {
		registry_.addMUC(new JID("foo@bar"));
		JID testJID = new JID("foo@bar");

		assertEquals(("foo@bar"), resolver_.jidToNick(testJID));
	}

	@Test
	public void testNoMatch() {
		JID testJID = new JID("foo@bar/baz");

		assertEquals(("foo@bar"), resolver_.jidToNick(testJID));
	}

	@Test
	public void testZeroLengthMatch() {
		JID testJID = new JID("foo@bar/baz");
		xmppRoster_.addContact(testJID, "", groups_, RosterItemPayload.Subscription.Both);
		assertEquals(("foo@bar"), resolver_.jidToNick(testJID));
	}

	@Test
	public void testMatch() {
		JID testJID = new JID("foo@bar/baz");
		xmppRoster_.addContact(testJID, "Test", groups_, RosterItemPayload.Subscription.Both);

		assertEquals(("Test"), resolver_.jidToNick(testJID));
	}

	@Test
	public void testOverwrittenMatch() {
		JID testJID = new JID("foo@bar/baz");
		xmppRoster_.addContact(testJID, "FailTest", groups_, RosterItemPayload.Subscription.Both);
		xmppRoster_.addContact(testJID, "Test", groups_, RosterItemPayload.Subscription.Both);

		assertEquals(("Test"), resolver_.jidToNick(testJID));
	}

	@Test
	public void testRemovedMatch() {
		JID testJID = new JID("foo@bar/baz");
		xmppRoster_.addContact(testJID, "FailTest", groups_, RosterItemPayload.Subscription.Both);
		xmppRoster_.removeContact(testJID);
		assertEquals(("foo@bar"), resolver_.jidToNick(testJID));
	}

	@Test
	public void testOwnNickFullOnly() {
		populateOwnVCard("", "", "Kevin Smith");
		assertEquals(("Kevin Smith"), resolver_.jidToNick(ownJID_));
	}

	@Test
	public void testOwnNickGivenAndFull() {
		populateOwnVCard("", "Kevin", "Kevin Smith");
		assertEquals(("Kevin"), resolver_.jidToNick(ownJID_));
	}

	@Test
	public void testOwnNickNickEtAl() {
		populateOwnVCard("Kev", "Kevin", "Kevin Smith");
		assertEquals(("Kev"), resolver_.jidToNick(ownJID_));
	}	
}