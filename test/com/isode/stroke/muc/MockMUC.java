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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.isode.stroke.elements.Form;
import com.isode.stroke.elements.MUCOccupant;
import com.isode.stroke.jid.JID;

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
	@Override
	public JID getJID() {
		return ownMUCJID.toBare();
	}

	/**
	 * Returns if the room is unlocked and other people can join the room.
	 * @return True if joinable by others; false otherwise.
	 */
	@Override
	public boolean isUnlocked() {
		return true;
	}

	@Override
	public void joinAs(final String nick) {}
	@Override
	public void joinWithContextSince(final String nick, final Date since) {}
	/*public void queryRoomInfo(); */
	/*public void queryRoomItems(); */
	/*public String getCurrentNick(); */
	@Override
	public Map<String, MUCOccupant> getOccupants() { 
		return occupants_; 
	}

	@Override
	public void changeNickname(final String newNickname) {}
	@Override
	public void part() {}
        public void disconnect() {}
	/*public void handleIncomingMessage(Message::ref message); */
	/** Expose public so it can be called when e.g. user goes offline */
	@Override
	public void handleUserLeft(LeavingType l) {}

	/**
	 * Get occupant information. 
	 */
	@Override
	public MUCOccupant getOccupant(final String nick) {
		return occupants_.get(nick);
	}
	@Override
	public boolean hasOccupant(final String nick){
		return occupants_.containsKey(nick);
	}

	@Override
	public void kickOccupant(final JID jid) {}

	@Override
	public void changeOccupantRole(final JID jid, MUCOccupant.Role newRole) {
		String resource = jid.getResource();
		if(occupants_.containsKey(resource)) {
			MUCOccupant old = occupants_.get(resource);
			occupants_.remove(resource);
			occupants_.put(resource, new MUCOccupant(old.getNick(), newRole, old.getAffiliation()));
			onOccupantRoleChanged.emit(resource, occupants_.get(resource), old.getRole());
		}
	}

	@Override
	public void requestAffiliationList(MUCOccupant.Affiliation aff) {}

	@Override
	public void changeAffiliation(final JID jid, MUCOccupant.Affiliation newAffilation) {
		String resource = jid.getResource();
		if(occupants_.containsKey(resource)) {
			MUCOccupant old = occupants_.get(resource);
			occupants_.remove(resource);
			occupants_.put(resource, new MUCOccupant(old.getNick(), old.getRole(), newAffilation));
			onOccupantAffiliationChanged.emit(resource, newAffilation, old.getAffiliation());
		}
	}

	@Override
	public void changeSubject(final String subject) {}
	@Override
	public void requestConfigurationForm() {}
	@Override
	public void configureRoom(Form f) {}
	@Override
	public void cancelConfigureRoom() {}
	@Override
	public void destroyRoom() {}
	/** Send an invite for the person to join the MUC */
	@Override
	public void invitePerson(final JID person, final String reason, boolean isImpromptu, boolean isReuseChat) {}
	@Override
	public void setCreateAsReservedIfNew() {}
	@Override
	public void setPassword(final String password) {}

	protected boolean isFromMUC(final JID j) {
		return (ownMUCJID.compare(j, JID.CompareType.WithoutResource) == 0);
	}

	protected String getOwnNick() {
		return ownMUCJID.getResource();
	}
}
