/*
 * Copyright (c) 2010-2016 Isode Limited.
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
import com.isode.stroke.elements.Payload;
import com.isode.stroke.client.DummyStanzaChannel;
import com.isode.stroke.presence.DirectedPresenceSender;
import com.isode.stroke.presence.StanzaChannelPresenceSender;
import com.isode.stroke.presence.PayloadAddingPresenceSender;
import com.isode.stroke.presence.PresenceOracle;
import com.isode.stroke.presence.SubscriptionManager;
import com.isode.stroke.jid.JID;
import com.isode.stroke.roster.XMPPRoster;
import com.isode.stroke.roster.XMPPRosterImpl;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot2;
import com.isode.stroke.signals.Slot3;
import com.isode.stroke.signals.Slot1;

import java.util.Collection;
import java.util.Vector;

public class PresenceOracleTest {

	private class SubscriptionRequestInfo {
		public JID jid;
		public String reason;

		public SubscriptionRequestInfo() {
			jid = new JID();
			reason = "";
		}
	};

	private PresenceOracle oracle_;
	private SubscriptionManager subscriptionManager_;
	private DummyStanzaChannel stanzaChannel_;
	private XMPPRoster xmppRoster_;
	private Collection<Presence> changes = new Vector<Presence>();
	private Collection<SubscriptionRequestInfo> subscriptionRequests = new Vector<SubscriptionRequestInfo>();
	private JID user1;
	private JID user1alt;
	private JID user2;

	private Presence makeOnline(final String resource, int priority) {
		Presence presence = new Presence();
		presence.setPriority(priority);
		presence.setFrom(new JID("alice@wonderland.lit/" + resource));
		return presence;
	}

	private Presence makeOffline(final String resource) {
		Presence presence = new Presence();
		presence.setFrom(new JID("alice@wonderland.lit" + resource));
		presence.setType(Presence.Type.Unavailable);
		return presence;
	}

	private void handlePresenceChange(Presence newPresence) {
		changes.add(newPresence);
	}
		
	private void handlePresenceSubscriptionRequest(final JID jid, final String reason) {
		SubscriptionRequestInfo subscriptionRequest = new SubscriptionRequestInfo();
		subscriptionRequest.jid = jid;
		subscriptionRequest.reason = reason;
		subscriptionRequests.add(subscriptionRequest);
	}

	private Presence createPresence(final JID jid) {
		Presence sentPresence = new Presence("blarb");
		sentPresence.setFrom(jid);
		return sentPresence;
	}

	@Before
	public void setUp() {
		stanzaChannel_ = new DummyStanzaChannel();
		xmppRoster_ = new XMPPRosterImpl();
		oracle_ = new PresenceOracle(stanzaChannel_,xmppRoster_);
		oracle_.onPresenceChange.connect(new Slot1<Presence>() {
			@Override
			public void call(Presence p) {
				handlePresenceChange(p);
			}
		});
		subscriptionManager_ = new SubscriptionManager(stanzaChannel_);
		subscriptionManager_.onPresenceSubscriptionRequest.connect(new Slot3<JID, String, Presence>() {
			@Override
			public void call(JID j, String s, Presence p) {
				handlePresenceSubscriptionRequest(j, s);
			}
		});
		user1 = new JID("user1@foo.com/Foo");
		user1alt = new JID("user1@foo.com/Bar");
		user2 = new JID("user2@bar.com/Bar");
	}

	@Test
	public void testHighestPresenceSingle() {
		JID bareJID = new JID("alice@wonderland.lit");
		Presence fiveOn = makeOnline("blah", 5);
		Presence fiveOff = makeOffline("/blah");
		assertEquals(null, oracle_.getHighestPriorityPresence(bareJID));
		stanzaChannel_.onPresenceReceived.emit(fiveOn);
		assertEquals(fiveOn, oracle_.getHighestPriorityPresence(bareJID));
		stanzaChannel_.onPresenceReceived.emit(fiveOff);
		assertEquals(fiveOff, oracle_.getHighestPriorityPresence(bareJID));
	}

	@Test
	public void testHighestPresenceMultiple() {
		JID bareJID = new JID("alice@wonderland.lit");
		Presence fiveOn = makeOnline("blah", 5);
		Presence fiveOff = makeOffline("/blah");
		Presence tenOn = makeOnline("bert", 10);
		Presence tenOff = makeOffline("/bert");
		stanzaChannel_.onPresenceReceived.emit(fiveOn);
		stanzaChannel_.onPresenceReceived.emit(tenOn);
		assertEquals(tenOn, oracle_.getHighestPriorityPresence(bareJID));
		stanzaChannel_.onPresenceReceived.emit(fiveOff);
		assertEquals(tenOn, oracle_.getHighestPriorityPresence(bareJID));
		stanzaChannel_.onPresenceReceived.emit(fiveOn);
		stanzaChannel_.onPresenceReceived.emit(tenOff);
		assertEquals(fiveOn, oracle_.getHighestPriorityPresence(bareJID));
	}

	@Test
	public void testHighestPresenceGlobal() {
		JID bareJID = new JID("alice@wonderland.lit");
		Presence fiveOn = makeOnline("blah", 5);
		Presence fiveOff = makeOffline("/blah");
		Presence tenOn = makeOnline("bert", 10);
		Presence allOff = makeOffline("");
		stanzaChannel_.onPresenceReceived.emit(fiveOn);
		stanzaChannel_.onPresenceReceived.emit(tenOn);
		stanzaChannel_.onPresenceReceived.emit(allOff);
		assertEquals(allOff, oracle_.getHighestPriorityPresence(bareJID));
	}

	@Test
	public void testHighestPresenceChangePriority() {
		JID bareJID = new JID("alice@wonderland.lit");
		Presence fiveOn = makeOnline("blah", 5);
		Presence fiveOff = makeOffline("/blah");
		Presence tenOn = makeOnline("bert", 10);
		Presence tenOnThree = makeOnline("bert", 3);
		Presence tenOff = makeOffline("/bert");
		stanzaChannel_.onPresenceReceived.emit(fiveOn);
		stanzaChannel_.onPresenceReceived.emit(tenOn);
		stanzaChannel_.onPresenceReceived.emit(tenOnThree);
		assertEquals(fiveOn, oracle_.getHighestPriorityPresence(bareJID));
		stanzaChannel_.onPresenceReceived.emit(fiveOff);
		assertEquals(tenOnThree, oracle_.getHighestPriorityPresence(bareJID));
		stanzaChannel_.onPresenceReceived.emit(fiveOn);
		assertEquals(fiveOn, oracle_.getHighestPriorityPresence(bareJID));
	}

	@Test
	public void testReceivePresence() {
		Presence sentPresence = createPresence(user1);
		stanzaChannel_.onPresenceReceived.emit(sentPresence);

		assertEquals(1, (changes.size()));
		assertEquals(0, (subscriptionRequests.size()));
		assertEquals(sentPresence, changes.toArray()[0]);
		assertEquals(sentPresence, oracle_.getLastPresence(user1));
	}

	@Test
	public void testReceivePresenceFromDifferentResources() {
		Presence sentPresence1 = createPresence(user1);
		Presence sentPresence2 = createPresence(user1alt);
		stanzaChannel_.onPresenceReceived.emit(sentPresence1);
		stanzaChannel_.onPresenceReceived.emit(sentPresence2);

		assertEquals(sentPresence1, oracle_.getLastPresence(user1));
		assertEquals(sentPresence2, oracle_.getLastPresence(user1alt));
	}
		
	@Test
	public void testSubscriptionRequest() {
		String reasonText = "Because I want to";
		JID sentJID = new JID("me@example.com");

		Presence sentPresence = new Presence();
		sentPresence.setType(Presence.Type.Subscribe);
		sentPresence.setFrom(sentJID);
		sentPresence.setStatus(reasonText);
		stanzaChannel_.onPresenceReceived.emit(sentPresence);

		assertEquals(0, (changes.size()));
		assertEquals(1, (subscriptionRequests.size()));
		assertEquals(sentJID, ((SubscriptionRequestInfo)subscriptionRequests.toArray()[0]).jid);
		assertEquals(reasonText, ((SubscriptionRequestInfo)subscriptionRequests.toArray()[0]).reason);
	}

	@Test
	public void testReconnectResetsPresences() {
		Presence sentPresence = createPresence(user1);
		stanzaChannel_.onPresenceReceived.emit(sentPresence);
		stanzaChannel_.setAvailable(false);
		stanzaChannel_.setAvailable(true);

		assertNull(oracle_.getLastPresence(user1));
	}	
}