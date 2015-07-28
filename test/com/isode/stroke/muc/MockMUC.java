/*
 * Copyright (c) 2013 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.muc;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.isode.stroke.client.StanzaChannel;
import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.elements.CapsInfo;
import com.isode.stroke.elements.Form;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.elements.MUCAdminPayload;
import com.isode.stroke.elements.MUCDestroyPayload;
import com.isode.stroke.elements.MUCInvitationPayload;
import com.isode.stroke.elements.MUCItem;
import com.isode.stroke.elements.MUCOccupant;
import com.isode.stroke.elements.MUCOwnerPayload;
import com.isode.stroke.elements.MUCPayload;
import com.isode.stroke.elements.MUCUserPayload;
import com.isode.stroke.elements.Message;
import com.isode.stroke.elements.Presence;
import com.isode.stroke.jid.JID;
import com.isode.stroke.jid.JID.CompareType;
import com.isode.stroke.presence.DirectedPresenceSender;
import com.isode.stroke.queries.GenericRequest;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.signals.Signal;
import com.isode.stroke.signals.Signal1;
import com.isode.stroke.signals.Signal2;
import com.isode.stroke.signals.Signal3;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.signals.Slot2;

public class MockMUC extends MUC {

	private JID ownMUCJID = new JID();
	private Map<String, MUCOccupant> occupants_ = new HashMap<String, MUCOccupant>();

	public MockMUC(JID muc) {
		ownMUCJID = muc;
	}

	/**
	 * Cause a user to appear to have entered the room. For testing only.
	 */
	public void insertOccupant(final MUCOccupant occupant) {
		occupants_.put(occupant.getNick(), occupant);
		onOccupantJoined.emit(occupant);
	}

	/**
	 * Returns the (bare) JID of the MUC.
	 */
	public JID getJID() {
		return ownMUCJID.toBare();
	}

	/**
	 * Returns if the room is unlocked and other people can join the room.
	 * @return True if joinable by others; false otherwise.
	 */
	public boolean isUnlocked() {
		return true;
	}

	public void joinAs(final String nick) {}
	public void joinWithContextSince(final String nick, final Date since) {}
	/*public void queryRoomInfo(); */
	/*public void queryRoomItems(); */
	/*public String getCurrentNick(); */
	public Map<String, MUCOccupant> getOccupants() { 
		return occupants_; 
	}

	public void changeNickname(final String newNickname) {}
	public void part() {}
        public void disconnect() {}
	/*public void handleIncomingMessage(Message::ref message); */
	/** Expose public so it can be called when e.g. user goes offline */
	public void handleUserLeft(LeavingType l) {}

	/**
	 * Get occupant information. 
	 */
	public MUCOccupant getOccupant(final String nick) {
		return occupants_.get(nick);
	}
	public boolean hasOccupant(final String nick){
		return occupants_.containsKey(nick);
	}

	public void kickOccupant(final JID jid) {}

	public void changeOccupantRole(final JID jid, MUCOccupant.Role newRole) {
		String resource = jid.getResource();
		if(occupants_.containsKey(resource)) {
			MUCOccupant old = occupants_.get(resource);
			occupants_.remove(resource);
			occupants_.put(resource, new MUCOccupant(old.getNick(), newRole, old.getAffiliation()));
			onOccupantRoleChanged.emit(resource, occupants_.get(resource), old.getRole());
		}
	}

	public void requestAffiliationList(MUCOccupant.Affiliation aff) {}

	public void changeAffiliation(final JID jid, MUCOccupant.Affiliation newAffilation) {
		String resource = jid.getResource();
		if(occupants_.containsKey(resource)) {
			MUCOccupant old = occupants_.get(resource);
			occupants_.remove(resource);
			occupants_.put(resource, new MUCOccupant(old.getNick(), old.getRole(), newAffilation));
			onOccupantAffiliationChanged.emit(resource, newAffilation, old.getAffiliation());
		}
	}

	public void changeSubject(final String subject) {}
	public void requestConfigurationForm() {}
	public void configureRoom(Form f) {}
	public void cancelConfigureRoom() {}
	public void destroyRoom() {}
	/** Send an invite for the person to join the MUC */
	public void invitePerson(final JID person, final String reason, boolean isImpromptu, boolean isReuseChat) {}
	public void setCreateAsReservedIfNew() {}
	public void setPassword(final String password) {}

	protected boolean isFromMUC(final JID j) {
		return (ownMUCJID.compare(j, JID.CompareType.WithoutResource) == 0);
	}

	protected String getOwnNick() {
		return ownMUCJID.getResource();
	}
}
