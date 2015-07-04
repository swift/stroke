/*
 * Copyright (c) 2010-2011 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.roster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import com.isode.stroke.roster.XMPPRosterSignalHandler;
import com.isode.stroke.roster.XMPPRosterController;
import com.isode.stroke.roster.XMPPRosterImpl;
import com.isode.stroke.roster.RosterMemoryStorage;
import com.isode.stroke.elements.RosterItemPayload;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.elements.RosterPayload;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.client.DummyStanzaChannel;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.jid.JID;
import java.util.Collection;
import java.util.ArrayList;

public class XMPPRosterControllerTest {

	private DummyStanzaChannel channel_;
	private IQRouter router_;
	private XMPPRosterImpl xmppRoster_;
	private XMPPRosterSignalHandler handler_;
	private RosterMemoryStorage rosterStorage_;
	private JID jid1_;
	private JID jid2_;
	private JID jid3_;

	@Before
	public void setUp() {
		channel_ = new DummyStanzaChannel();
		router_ = new IQRouter(channel_);
		router_.setJID(new JID("me@bla.com"));
		xmppRoster_ = new XMPPRosterImpl();
		handler_ = new XMPPRosterSignalHandler(xmppRoster_);
		rosterStorage_ = new RosterMemoryStorage();
		jid1_ = new JID("foo@bar.com");
		jid2_ = new JID("alice@wonderland.lit");
		jid3_ = new JID("jane@austen.lit");
	}

	private XMPPRosterController createController() {
		return new XMPPRosterController(router_, xmppRoster_, rosterStorage_);
	}

	@Test
	public void testGet_Response() {
		XMPPRosterController testling = createController();

		testling.requestRoster();
		RosterPayload payload = new RosterPayload();
		payload.addItem(new RosterItemPayload(jid1_, "Bob", RosterItemPayload.Subscription.Both));
		payload.addItem(new RosterItemPayload(jid2_, "Alice", RosterItemPayload.Subscription.Both));
		channel_.onIQReceived.emit(IQ.createResult(new JID("foo@bar.com"), channel_.sentStanzas.get(0).getID(), payload));

		assertEquals(2, handler_.getEventCount());
		assertNotNull(xmppRoster_.getItem(jid1_));
		assertNotNull(xmppRoster_.getItem(jid2_));
	}

	@Test
	public void testGet_EmptyResponse() {
		XMPPRosterController controller = new XMPPRosterController(router_, xmppRoster_, rosterStorage_);

		controller.requestRoster();

		channel_.onIQReceived.emit(IQ.createResult(new JID("baz@fum.com/dum"), channel_.sentStanzas.get(0).getID(), null));
	}

	@Test
	public void testAdd() {
		XMPPRosterController controller = new XMPPRosterController(router_, xmppRoster_, rosterStorage_);

		RosterPayload payload = new RosterPayload();
		payload.addItem(new RosterItemPayload(jid1_, "Bob", RosterItemPayload.Subscription.Both));
		channel_.onIQReceived.emit(IQ.createRequest(IQ.Type.Set, new JID(), "eou", payload));

		assertEquals(XMPPRosterEvents.Add, handler_.getLastEvent());
		assertEquals(jid1_, handler_.getLastJID());
		assertEquals(0, xmppRoster_.getGroupsForJID(jid1_).size());
		assertTrue(xmppRoster_.containsJID(jid1_));
		assertEquals("Bob", xmppRoster_.getNameForJID(jid1_));
	}

	@Test
	public void testGet_NoRosterInStorage() {
		XMPPRosterController testling = createController();
		testling.setUseVersioning(true);

		testling.requestRoster();

		RosterPayload roster = channel_.sentStanzas.get(0).getPayload(new RosterPayload());
		assertNotNull(roster.getVersion());
		assertEquals("", roster.getVersion());
	}

	@Test
	public void testGet_NoVersionInStorage() {
		XMPPRosterController testling = createController();
		testling.setUseVersioning(true);
		rosterStorage_.setRoster(new RosterPayload());

		testling.requestRoster();

		RosterPayload roster = channel_.sentStanzas.get(0).getPayload(new RosterPayload());
		assertNotNull(roster.getVersion());
		assertEquals("", roster.getVersion());
	}

	@Test
	public void testGet_VersionInStorage() {
		XMPPRosterController testling = createController();
		testling.setUseVersioning(true);
		RosterPayload payload = new RosterPayload();
		payload.setVersion("foover");
		rosterStorage_.setRoster(payload);

		testling.requestRoster();

		RosterPayload roster = channel_.sentStanzas.get(0).getPayload(new RosterPayload());
		assertNotNull(roster.getVersion());
		assertEquals("foover", roster.getVersion());
	}

	@Test
	public void testGet_ServerDoesNotSupportVersion() {
		XMPPRosterController testling = createController();
		RosterPayload payload = new RosterPayload();
		payload.setVersion("foover");
		rosterStorage_.setRoster(payload);

		testling.requestRoster();

		RosterPayload roster = channel_.sentStanzas.get(0).getPayload(new RosterPayload());
		assertNull(roster.getVersion());
	}

	@Test
	public void testGet_ResponseWithoutNewVersion() {
		XMPPRosterController testling = createController();
		testling.setUseVersioning(true);
		RosterPayload storedRoster = new RosterPayload();
		storedRoster.setVersion("version10");
		storedRoster.addItem(new RosterItemPayload(jid1_, "Bob", RosterItemPayload.Subscription.Both));
		storedRoster.addItem(new RosterItemPayload(jid2_, "Alice", RosterItemPayload.Subscription.Both));
		rosterStorage_.setRoster(storedRoster);
		testling.requestRoster();

		channel_.onIQReceived.emit(IQ.createResult(new JID("foo@bar.com"), channel_.sentStanzas.get(0).getID(), null));

		assertEquals(2, handler_.getEventCount());
		assertNotNull(xmppRoster_.getItem(jid1_));
		assertNotNull(xmppRoster_.getItem(jid2_));
		assertEquals(XMPPRosterEvents.Add, handler_.getLastEvent());
		assertEquals(jid2_, handler_.getLastJID());
		assertNotNull(rosterStorage_.getRoster());
		assertNotNull(rosterStorage_.getRoster().getVersion());
		assertEquals("version10", rosterStorage_.getRoster().getVersion());
		assertNotNull(rosterStorage_.getRoster().getItem(jid1_));
		assertNotNull(rosterStorage_.getRoster().getItem(jid2_));
	}

	@Test
	public void testGet_ResponseWithNewVersion() {
		XMPPRosterController testling = createController();
		testling.setUseVersioning(true);
		RosterPayload storedRoster = new RosterPayload();
		storedRoster.setVersion("version10");
		storedRoster.addItem(new RosterItemPayload(jid1_, "Bob", RosterItemPayload.Subscription.Both));
		rosterStorage_.setRoster(storedRoster);
		testling.requestRoster();

		RosterPayload serverRoster = new RosterPayload();
		serverRoster.setVersion("version12");
		serverRoster.addItem(new RosterItemPayload(jid2_, "Alice", RosterItemPayload.Subscription.Both));
		Collection<String> groups = new ArrayList<String>();
		groups.add("foo");
		groups.add("bar");
		serverRoster.addItem(new RosterItemPayload(jid3_, "Rabbit", RosterItemPayload.Subscription.Both, groups));
		channel_.onIQReceived.emit(IQ.createResult(new JID("foo@bar.com"), channel_.sentStanzas.get(0).getID(), serverRoster));


		assertEquals(2, handler_.getEventCount());
		assertNull(xmppRoster_.getItem(jid1_));
		assertNotNull(xmppRoster_.getItem(jid2_));
		assertNotNull(xmppRoster_.getItem(jid3_));
		assertEquals(jid3_, handler_.getLastJID());
		assertEquals(XMPPRosterEvents.Add, handler_.getLastEvent());
		assertNotNull(rosterStorage_.getRoster());
		assertNotNull(rosterStorage_.getRoster().getVersion());
		assertEquals("version12", rosterStorage_.getRoster().getVersion());
		assertNull(rosterStorage_.getRoster().getItem(jid1_));
		assertNotNull(rosterStorage_.getRoster().getItem(jid2_));
		assertNotNull(rosterStorage_.getRoster().getItem(jid3_));
		assertEquals(2, rosterStorage_.getRoster().getItem(jid3_).getGroups().size());
	}

	@Test
	public void testAddFromNonAccount() {
		XMPPRosterController testling = createController();

		RosterPayload payload = new RosterPayload();
		payload.addItem(new RosterItemPayload(jid1_, "Bob", RosterItemPayload.Subscription.Both));
		IQ request = IQ.createRequest(IQ.Type.Set, new JID(), "eou", payload);
		request.setFrom(jid2_);
		channel_.onIQReceived.emit(request);

		assertEquals(XMPPRosterEvents.None, handler_.getLastEvent());
	}

	@Test
	public void testModify() {
		XMPPRosterController controller = new XMPPRosterController(router_, xmppRoster_, rosterStorage_);
		RosterPayload payload1 = new RosterPayload();
		payload1.addItem(new RosterItemPayload(jid1_, "Bob", RosterItemPayload.Subscription.Both));
		channel_.onIQReceived.emit(IQ.createRequest(IQ.Type.Set, new JID(), "id1", payload1));

		assertEquals(XMPPRosterEvents.Add, handler_.getLastEvent());
		assertEquals(jid1_, handler_.getLastJID());
		handler_.reset();

		RosterPayload payload2 = new RosterPayload();
		payload2.addItem(new RosterItemPayload(jid1_, "Bob2", RosterItemPayload.Subscription.Both));
		channel_.onIQReceived.emit(IQ.createRequest(IQ.Type.Set, new JID(), "id2", payload2));

		assertEquals(XMPPRosterEvents.Update, handler_.getLastEvent());
		assertEquals(jid1_, handler_.getLastJID());

		assertEquals("Bob2", xmppRoster_.getNameForJID(jid1_));
	}

	@Test
	public void testRemove() {
		XMPPRosterController controller = new XMPPRosterController(router_, xmppRoster_, rosterStorage_);
		RosterPayload payload1 = new RosterPayload();
		payload1.addItem(new RosterItemPayload(jid1_, "Bob", RosterItemPayload.Subscription.Both));
		channel_.onIQReceived.emit(IQ.createRequest(IQ.Type.Set, new JID(), "id1", payload1));

		assertEquals(XMPPRosterEvents.Add, handler_.getLastEvent());
		assertEquals(jid1_, handler_.getLastJID());
		handler_.reset();

		RosterPayload payload2 = new RosterPayload();
		payload2.addItem(new RosterItemPayload(jid1_, "Bob", RosterItemPayload.Subscription.Remove));
		channel_.onIQReceived.emit(IQ.createRequest(IQ.Type.Set, new JID(), "id2", payload2));
		assertFalse(xmppRoster_.containsJID(jid1_));
		assertEquals(XMPPRosterEvents.Remove, handler_.getLastEvent());
		assertEquals(jid1_, handler_.getLastJID());
	}

	@Test
	public void testRemove_RosterStorageUpdated() {
		XMPPRosterController testling = createController();
		testling.setUseVersioning(true);
		RosterPayload storedRoster = new RosterPayload();
		storedRoster.setVersion("version10");
		storedRoster.addItem(new RosterItemPayload(jid1_, "Bob", RosterItemPayload.Subscription.Both));
		storedRoster.addItem(new RosterItemPayload(jid2_, "Alice", RosterItemPayload.Subscription.Both));
		rosterStorage_.setRoster(storedRoster);
		testling.requestRoster();
		channel_.onIQReceived.emit(IQ.createResult(new JID("foo@bar.com"), channel_.sentStanzas.get(0).getID(), null));

		RosterPayload payload2 = new RosterPayload();
		payload2.setVersion("version15");
		payload2.addItem(new RosterItemPayload(jid1_, "Bob", RosterItemPayload.Subscription.Remove));
		channel_.onIQReceived.emit(IQ.createRequest(IQ.Type.Set, new JID(), "id2", payload2));

		assertNotNull(rosterStorage_.getRoster());
		assertNotNull(rosterStorage_.getRoster().getVersion());
		assertEquals("version15", rosterStorage_.getRoster().getVersion());
		assertNull(rosterStorage_.getRoster().getItem(jid1_));
		assertNotNull(rosterStorage_.getRoster().getItem(jid2_));
	}

	@Test
	public void testMany() {
		XMPPRosterController controller = new XMPPRosterController(router_, xmppRoster_, rosterStorage_);
		RosterPayload payload1 = new RosterPayload();
		payload1.addItem(new RosterItemPayload(jid1_, "Bob", RosterItemPayload.Subscription.Both));
		channel_.onIQReceived.emit(IQ.createRequest(IQ.Type.Set, new JID(), "id1", payload1));

		assertEquals(XMPPRosterEvents.Add, handler_.getLastEvent());
		assertEquals(jid1_, handler_.getLastJID());
		handler_.reset();

		RosterPayload payload2 = new RosterPayload();
		payload2.addItem(new RosterItemPayload(jid2_, "Alice", RosterItemPayload.Subscription.Both));
		channel_.onIQReceived.emit(IQ.createRequest(IQ.Type.Set, new JID(), "id2", payload2));

		assertEquals(XMPPRosterEvents.Add, handler_.getLastEvent());
		assertEquals(jid2_, handler_.getLastJID());
		handler_.reset();

		RosterPayload payload3 = new RosterPayload();
		payload3.addItem(new RosterItemPayload(jid1_, "Ernie", RosterItemPayload.Subscription.Both));
		channel_.onIQReceived.emit(IQ.createRequest(IQ.Type.Set, new JID(), "id3", payload3));

		assertEquals(XMPPRosterEvents.Update, handler_.getLastEvent());
		assertEquals(jid1_, handler_.getLastJID());
		handler_.reset();

		RosterPayload payload4 = new RosterPayload();
		RosterItemPayload item = new RosterItemPayload(jid3_, "Jane", RosterItemPayload.Subscription.Both);
		String janesGroup = "Jane's Group";
		item.addGroup(janesGroup);
		payload4.addItem(item);
		channel_.onIQReceived.emit(IQ.createRequest(IQ.Type.Set, new JID(), "id4", payload4));

		assertEquals(XMPPRosterEvents.Add, handler_.getLastEvent());
		assertEquals(jid3_, handler_.getLastJID());
		assertEquals(1, xmppRoster_.getGroupsForJID(jid3_).size());
		assertEquals(janesGroup, xmppRoster_.getGroupsForJID(jid3_).toArray()[0]);
		handler_.reset();

		RosterPayload payload5 = new RosterPayload();
		payload5.addItem(new RosterItemPayload(jid1_, "Bob", RosterItemPayload.Subscription.Remove));
		channel_.onIQReceived.emit(IQ.createRequest(IQ.Type.Set, new JID(), "id5", payload5));
		assertFalse(xmppRoster_.containsJID(jid1_));
		assertEquals(XMPPRosterEvents.Remove, handler_.getLastEvent());
		assertEquals(jid1_, handler_.getLastJID());
		handler_.reset();

		RosterPayload payload6 = new RosterPayload();
		RosterItemPayload item2 = new RosterItemPayload(jid2_, "Little Alice", RosterItemPayload.Subscription.Both);
		String alicesGroup = "Alice's Group";
		item2.addGroup(alicesGroup);
		payload6.addItem(item2);
		channel_.onIQReceived.emit(IQ.createRequest(IQ.Type.Set, new JID(), "id6", payload6));
		assertEquals(XMPPRosterEvents.Update, handler_.getLastEvent());
		assertEquals(jid2_, handler_.getLastJID());
		assertEquals("Little Alice", xmppRoster_.getNameForJID(jid2_));
		assertEquals("Jane", xmppRoster_.getNameForJID(jid3_));
		assertEquals(1, xmppRoster_.getGroupsForJID(jid2_).size());
		assertEquals(alicesGroup, xmppRoster_.getGroupsForJID(jid2_).toArray()[0]);
		assertEquals(1, xmppRoster_.getGroupsForJID(jid3_).size());
		assertEquals(janesGroup, xmppRoster_.getGroupsForJID(jid3_).toArray()[0]);
		handler_.reset();
	}
}