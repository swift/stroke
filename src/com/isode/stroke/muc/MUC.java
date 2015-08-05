/*
 * Copyright (c) 2010-2014 Isode Limited.
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
import java.util.Map;
import java.util.Vector;

import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.elements.Form;
import com.isode.stroke.elements.MUCOccupant;
import com.isode.stroke.elements.Presence;
import com.isode.stroke.jid.JID;
import com.isode.stroke.signals.Signal;
import com.isode.stroke.signals.Signal1;
import com.isode.stroke.signals.Signal2;
import com.isode.stroke.signals.Signal3;

public abstract class MUC {

	public enum JoinResult { JoinSucceeded, JoinFailed };
	public enum LeavingType { LeavePart, LeaveKick, LeaveBan, LeaveDestroy, LeaveNotMember, Disconnect };

	/**
	 * Returns the (bare) JID of the MUC.
	 */
	public abstract JID getJID();

	/**
	 * Returns if the room is unlocked and other people can join the room.
	 * @return True if joinable by others; false otherwise.
	 */
	public abstract boolean isUnlocked();

	public abstract void joinAs(final String nick);
	public abstract void joinWithContextSince(final String nick, final Date since);
	/*public abstract void queryRoomInfo(); */
	/*public abstract void queryRoomItems(); */
	/*public abstract String getCurrentNick(); */
	public abstract Map<String, MUCOccupant> getOccupants();
	public abstract void changeNickname(final String newNickname);
	public abstract void part();
	/**
	* Disconnect signals for this MUC.
	* Java-specific method (not in Swiften) required so that any connected
	* signals can be disconnected when the object is no longer required.
	* While any signals are still connected, the MUC object will not be
	* eligible for garbage collection.
	*/
        public abstract void disconnect();
	/*public abstract void handleIncomingMessage(Message::ref message); */
	/** Expose public so it can be called when e.g. user goes offline */
	public abstract void handleUserLeft(LeavingType l);
	/** Get occupant information*/
	public abstract MUCOccupant getOccupant(final String nick);
	public abstract boolean hasOccupant(final String nick);
	public abstract void kickOccupant(final JID jid);
	public abstract void changeOccupantRole(final JID jid, MUCOccupant.Role role);
	public abstract void requestAffiliationList(MUCOccupant.Affiliation aff);
	public abstract void changeAffiliation(final JID jid, MUCOccupant.Affiliation affiliation);
	public abstract void changeSubject(final String subject);
	public abstract void requestConfigurationForm();
	public abstract void configureRoom(Form f);
	public abstract void cancelConfigureRoom();
	public abstract void destroyRoom();

	/**
	 * Invite the person with given JID to the chat room
	 * @param person Jabber ID to the person to invite, not null
	 */
	public void invitePerson(JID person) {
		invitePerson(person, "", false, false);
	}

	/**
	 * Invite the person with given JID to the chat room
	 * @param person Jabber ID to the person to invite, not null
	 * @param reason join reason, not null
	 */
	public void invitePerson(JID person, String reason) {
		invitePerson(person, reason, false, false);
	}
	
	/**
	 * Send an invite for the person to join the MUC 
	 * @param person Jabber ID of the person to invite, not null
	 * @param reason join reason, not null
	 * @param isImpromptu 
	 */
	public void invitePerson(JID person, String reason, boolean isImpromptu) {
		invitePerson(person, reason, isImpromptu, false);
	}

	/**
	 * Send an invite for the person to join the MUC 
	 * @param person Jabber ID of the person to invite, not null
	 * @param reason join reason, not null
	 * @param isImpromptu 
	 * @param isReuseChat 
	 */
	public abstract void invitePerson(final JID person, final String reason, boolean isImpromptu, boolean isReuseChat);
	public abstract void setCreateAsReservedIfNew();
	public abstract void setPassword(final String password);

	public final Signal1<String> onJoinComplete = new Signal1<String>();
	public final Signal1<ErrorPayload> onJoinFailed = new Signal1<ErrorPayload>();
	public final Signal3<ErrorPayload, JID, MUCOccupant.Role> onRoleChangeFailed = new Signal3<ErrorPayload, JID, MUCOccupant.Role>();
	public final Signal3<ErrorPayload, JID, MUCOccupant.Affiliation> onAffiliationChangeFailed = new Signal3<ErrorPayload, JID, MUCOccupant.Affiliation>();
	public final Signal1<ErrorPayload> onConfigurationFailed = new Signal1<ErrorPayload>();
	public final Signal1<ErrorPayload> onAffiliationListFailed = new Signal1<ErrorPayload>();
	public final Signal1<Presence> onOccupantPresenceChange = new Signal1<Presence>();
	public final Signal3<String, MUCOccupant, MUCOccupant.Role> onOccupantRoleChanged = new Signal3<String, MUCOccupant, MUCOccupant.Role>();
	public final Signal3<String, MUCOccupant.Affiliation /*new*/, MUCOccupant.Affiliation /*old*/> onOccupantAffiliationChanged = new Signal3<String, MUCOccupant.Affiliation, MUCOccupant.Affiliation>();
	public final Signal1<MUCOccupant> onOccupantJoined = new Signal1<MUCOccupant>();
	public final Signal2<String, String> onOccupantNicknameChanged = new Signal2<String, String>();
	public final Signal3<MUCOccupant, LeavingType, String> onOccupantLeft = new Signal3<MUCOccupant, LeavingType, String>();
	public final Signal1<Form> onConfigurationFormReceived = new Signal1<Form>();
	public final Signal2<MUCOccupant.Affiliation, Vector<JID> > onAffiliationListReceived = new Signal2<MUCOccupant.Affiliation, Vector<JID> >();
	public final Signal onUnlocked = new Signal();
	/* public final Signal1<MUCInfo> onInfoResult; */
	/* public final Signal1<blah> onItemsResult; */
}
