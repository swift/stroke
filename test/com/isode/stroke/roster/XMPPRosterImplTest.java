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
import com.isode.stroke.roster.XMPPRosterImpl;
import com.isode.stroke.elements.RosterItemPayload;
import com.isode.stroke.jid.JID;
import java.util.Collection;
import java.util.ArrayList;

public class XMPPRosterImplTest {

	private XMPPRosterImpl roster_;
	private XMPPRosterSignalHandler handler_;
	private JID jid1_;
	private JID jid2_;
	private JID jid3_;
	private Collection<String> groups1_ = new ArrayList<String>();
	private Collection<String> groups2_ = new ArrayList<String>();

	@Before
	public void setUp() {
		jid1_ = new JID("a@b.c");
		jid2_ = new JID("b@c.d");
		jid3_ = new JID("c@d.e");
		roster_ = new XMPPRosterImpl();
		handler_ = new XMPPRosterSignalHandler(roster_);
		groups1_.add("bobs");
		groups1_.add("berts");
		groups2_.add("ernies");
	}

	@Test
	public void testJIDAdded() {
		roster_.addContact(jid1_, "NewName", groups1_, RosterItemPayload.Subscription.Both);
		assertEquals(XMPPRosterEvents.Add, handler_.getLastEvent());
		assertEquals(jid1_, handler_.getLastJID());
		assertEquals("NewName", roster_.getNameForJID(jid1_));
		assertEquals(groups1_, roster_.getGroupsForJID(jid1_));
		handler_.reset();
		roster_.addContact(jid2_, "NameTwo", groups1_, RosterItemPayload.Subscription.Both);
		assertEquals(XMPPRosterEvents.Add, handler_.getLastEvent());
		assertEquals(jid2_, handler_.getLastJID());
		assertEquals("NameTwo", roster_.getNameForJID(jid2_));
		assertEquals("NewName", roster_.getNameForJID(jid1_));
		assertEquals(groups1_, roster_.getGroupsForJID(jid2_));
		assertEquals(groups1_, roster_.getGroupsForJID(jid1_));
		handler_.reset();
		roster_.addContact(jid3_, "NewName", groups2_, RosterItemPayload.Subscription.Both);
		assertEquals(XMPPRosterEvents.Add, handler_.getLastEvent());
		assertEquals(jid3_, handler_.getLastJID());
		assertEquals("NewName", roster_.getNameForJID(jid3_));
		assertEquals(groups2_, roster_.getGroupsForJID(jid3_));
	}

	@Test
	public void testJIDRemoved() {
		roster_.addContact(jid1_, "NewName", groups1_, RosterItemPayload.Subscription.Both);
		handler_.reset();
		roster_.removeContact(jid1_);
		assertEquals(XMPPRosterEvents.Remove, handler_.getLastEvent());
		assertEquals(jid1_, handler_.getLastJID());
		handler_.reset();
		roster_.addContact(jid1_, "NewName2", groups1_, RosterItemPayload.Subscription.Both);
		assertEquals(XMPPRosterEvents.Add, handler_.getLastEvent());
		assertEquals(jid1_, handler_.getLastJID());
		assertEquals("NewName2", roster_.getNameForJID(jid1_));
		roster_.addContact(jid2_, "NewName3", groups1_, RosterItemPayload.Subscription.Both);
		handler_.reset();
		roster_.removeContact(jid2_);
		assertEquals(XMPPRosterEvents.Remove, handler_.getLastEvent());
		assertEquals(jid2_, handler_.getLastJID());
		handler_.reset();
		roster_.removeContact(jid1_);
		assertEquals(XMPPRosterEvents.Remove, handler_.getLastEvent());
		assertEquals(jid1_, handler_.getLastJID());
	}

	@Test
	public void testJIDUpdated() {
		roster_.addContact(jid1_, "NewName", groups1_, RosterItemPayload.Subscription.Both);
		assertEquals(XMPPRosterEvents.Add, handler_.getLastEvent());
		assertEquals(jid1_, handler_.getLastJID());
		assertEquals("NewName", roster_.getNameForJID(jid1_));
		assertEquals(groups1_, roster_.getGroupsForJID(jid1_));
		handler_.reset();
		roster_.addContact(jid1_, "NameTwo", groups2_, RosterItemPayload.Subscription.Both);
		assertEquals(XMPPRosterEvents.Update, handler_.getLastEvent());
		assertEquals(jid1_, handler_.getLastJID());
		assertEquals("NameTwo", roster_.getNameForJID(jid1_));
		assertEquals(groups2_, roster_.getGroupsForJID(jid1_));
	}
}