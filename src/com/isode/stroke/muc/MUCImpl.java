/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
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
import java.util.Vector;

import com.isode.stroke.client.StanzaChannel;
import com.isode.stroke.elements.CapsInfo;
import com.isode.stroke.elements.ErrorPayload;
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
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.signals.Slot2;

/**
 * Class representing multi user chat room
 *
 */
public class MUCImpl extends MUC {

	private boolean createAsReservedIfNew;
	private IQRouter iqRouter_;
	private boolean joinComplete_;
	private Date joinSince_;
	private boolean joinSucceeded_;
	private MUCRegistry mucRegistry;
	private Map<String, MUCOccupant> occupants = new HashMap<String, MUCOccupant>();
	private JID ownMUCJID = new JID();
	private String password;
	private DirectedPresenceSender presenceSender;
	private StanzaChannel stanzaChannel;
	private boolean unlocking;
	private boolean isUnlocked_;
	private SignalConnection scopedConnection_;
	private Presence joinRequestPresence_ = new Presence();

	/**
	 * Create a MUC Session
	 * @param stanzaChannel stanza channel, not null
	 * @param iqRouter IQ stanza router, not null
	 * @param presenceSender Presence Sender, not null
	 * @param muc JID of the chat room, not null
	 * @param mucRegistry MUC registry, not null
	 * 
	 * @see #disconnect()
	 */
	public MUCImpl(StanzaChannel stanzaChannel, IQRouter iqRouter, 
			DirectedPresenceSender presenceSender, final JID muc, 
			MUCRegistry mucRegistry) {
		ownMUCJID = muc;
		this.stanzaChannel = stanzaChannel;
		this.iqRouter_ = iqRouter;
		this.presenceSender = presenceSender;
		this.mucRegistry = mucRegistry;
		this.createAsReservedIfNew = false;
		this.unlocking = false;
		this.isUnlocked_ = false;
		scopedConnection_ = this.stanzaChannel.onPresenceReceived.connect(
				new Slot1<Presence>() {
			@Override
			public void call(Presence p1) { 
				handleIncomingPresence(p1);
			}            
		});
	}

	/**
	 * Cancel the command for configuring room 
	 */
	@Override
	public void cancelConfigureRoom() {
		MUCOwnerPayload mucPayload = new MUCOwnerPayload();
		mucPayload.setPayload(new Form(Form.Type.CANCEL_TYPE));
		GenericRequest<MUCOwnerPayload> request = new GenericRequest<MUCOwnerPayload>(
				IQ.Type.Set, getJID(), mucPayload, iqRouter_);
		request.send();
	}

	/**
	 * Change the affiliation of the given Jabber ID.
	 * It must be called with the real JID, not the room JID.
	 * @param jid real jabber ID, not null
	 * @param affiliation new affiliation, not null 
	 */
	@Override
	public void changeAffiliation(final JID jid, final MUCOccupant.Affiliation affiliation) {
		final MUCAdminPayload mucPayload = new MUCAdminPayload();
		MUCItem item = new MUCItem();
		item.affiliation = affiliation;
		item.realJID = jid.toBare();
		mucPayload.addItem(item);
		GenericRequest<MUCAdminPayload> request = new GenericRequest<MUCAdminPayload>(
				IQ.Type.Set, getJID(), mucPayload, iqRouter_);
		request.onResponse.connect(new Slot2<MUCAdminPayload, ErrorPayload>() {
			@Override
			public void call(MUCAdminPayload p1, ErrorPayload p2) {
				handleAffiliationChangeResponse(p1,p2,jid,affiliation);
			}
		});
		request.send();
	}

	/**
	 * Change the role of the specified occupant. It must be
	 * called with the room JID, not the real JID. 
	 * @param jid Jabber ID of the occupant in the chat room, not null
	 * @param role new role, not null
	 */
	@Override
	public void changeOccupantRole(final JID jid, final MUCOccupant.Role role) {
		final MUCAdminPayload mucPayload = new MUCAdminPayload();
		MUCItem item = new MUCItem();
		item.role = role;
		item.nick = jid.getResource();
		mucPayload.addItem(item);
		GenericRequest<MUCAdminPayload> request = new GenericRequest<MUCAdminPayload>(
				IQ.Type.Set, getJID(), mucPayload, iqRouter_);
		request.onResponse.connect(new Slot2<MUCAdminPayload, ErrorPayload>() {
			@Override
			public void call(MUCAdminPayload p1, ErrorPayload p2) {
				handleOccupantRoleChangeResponse(p1,p2,jid,role); 
			} 
		});
		request.send();
	}

	/**
	 * Change the subject of the chat room
	 * @param subject new subject, not null
	 */
	@Override
	public void changeSubject(String subject) {
		Message message = new Message();
		message.setSubject(subject);
		message.setType(Message.Type.Groupchat);
		message.setTo(ownMUCJID.toBare());
		stanzaChannel.sendMessage(message);
	}

	/**
	 * Configure a chat room room
	 * @param form form to be used for configuration, not null
	 */
	@Override
	public void configureRoom(Form form) {
		MUCOwnerPayload mucPayload = new MUCOwnerPayload();
		mucPayload.setPayload(form);
		GenericRequest<MUCOwnerPayload> request = new GenericRequest<MUCOwnerPayload>(
				IQ.Type.Set, getJID(), mucPayload, iqRouter_);
		if (unlocking) {
			request.onResponse.connect(new Slot2<MUCOwnerPayload, ErrorPayload>() {
				@Override
				public void call(MUCOwnerPayload p1, ErrorPayload p2) {
					handleCreationConfigResponse(p1, p2);
				}
			});
		}else {
			request.onResponse.connect(new Slot2<MUCOwnerPayload, ErrorPayload>() {
				@Override
				public void call(MUCOwnerPayload p1, ErrorPayload p2) {
					handleConfigurationResultReceived(p1, p2);
				}
			});
		}
		request.send();
	}

	/**
	 * Destroy the chat room 
	 */
	@Override
	public void destroyRoom() {
		MUCOwnerPayload mucPayload = new MUCOwnerPayload();
		MUCDestroyPayload mucDestroyPayload = new MUCDestroyPayload();
		mucPayload.setPayload(mucDestroyPayload);
		GenericRequest<MUCOwnerPayload> request = new GenericRequest<MUCOwnerPayload>(
				IQ.Type.Set, getJID(), mucPayload, iqRouter_);
		request.onResponse.connect(new Slot2<MUCOwnerPayload, ErrorPayload>() {
			@Override
			public void call(MUCOwnerPayload p1, ErrorPayload p2) {
				handleConfigurationResultReceived(p1,p2);
			}
		});
		request.send();
	}

	/**
	 * Returns the (bare) JID of the MUC.
	 * @return bare JID(i.e. without resource)
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
		return isUnlocked_;
	}

	/**
	 * Get the MUC occupant with the given nick name
	 * @param nick nick name, not null
	 * @return MUC occupant if it exists or null if not
	 */
	@Override
	public MUCOccupant getOccupant(String nick) {
		return occupants.get(nick);
	}

	/**
	 * Determine if the room contains occupant with given nick
	 * @param nick given nick
	 * @return true if the occupant exists, false otherwise
	 */
	@Override
	public boolean hasOccupant(String nick) {
		return occupants.containsKey(nick);
	}

	@Override
	public Map<String, MUCOccupant> getOccupants() {
		return occupants;
	}

	/**
	 * Invite the person with give JID to the chat room
	 * @param person jabber ID o the person to invite,not nul
	 */
	@Override
	public void invitePerson(JID person) {
		invitePerson(person, "", false, false);
	}
	
	/**
	 * Send an invite for the person to join the MUC 
	 * @param person jabber ID of the person to invite, not null
	 * @param reason join reason, not null
	 * @param isImpromptu 
	 */
	@Override
	public void invitePerson(JID person, String reason, boolean isImpromptu) {
		invitePerson(person, reason, isImpromptu, false);
	}


	/**
	 * Send an invite for the person to join the MUC 
	 * @param person jabber ID of the person to invite, not null
	 * @param reason join reason, not null
	 * @param isImpromptu 
	 * @param isReuseChat 
	 */
	@Override
	public void invitePerson(JID person, String reason, boolean isImpromptu, boolean isReuseChat) {
		Message message = new Message();
		message.setTo(person);
		message.setType(Message.Type.Normal);
		MUCInvitationPayload invite = new MUCInvitationPayload();
		invite.setReason(reason);
		invite.setIsImpromptu(isImpromptu);
		invite.setIsContinuation(isReuseChat);
		invite.setJID(ownMUCJID.toBare());
		message.addPayload(invite);
		stanzaChannel.sendMessage(message);
	}

	/**
	 * Join the MUC with default context.
	 * @param nick nick name of the user, not null
	 */
	@Override
	public void joinAs(String nick) {
		joinSince_ = null;
		internalJoin(nick);
	}

	/**
	 * Join the MUC with context since date.
	 * @param nick nick name, not null
	 * @param since  date since the nick joined, not null
	 */
	@Override
	public void joinWithContextSince(String nick, Date since) { 
		joinSince_ = since;
		internalJoin(nick);
	}

	/**
	 * Kick the given occupant out of the chat room
	 * @param jid jabber ID of the user to kick, not null
	 */
	@Override
	public void kickOccupant(JID jid) {
		changeOccupantRole(jid, MUCOccupant.Role.NoRole);
	}    

	@Override
	public void changeNickname(final String newNickname) {
		Presence changeNicknamePresence = new Presence();
		changeNicknamePresence.setTo(new JID(ownMUCJID.toBare().toString() + "/" + newNickname));
		presenceSender.sendPresence(changeNicknamePresence);
	}

	/**
	 * Leave the chat room 
	 */
	@Override
	public void part() {
		presenceSender.removeDirectedPresenceReceiver(ownMUCJID, 
				DirectedPresenceSender.SendPresence.AndSendPresence);
		mucRegistry.removeMUC(getJID());
	}

	/**
	 * Send a request to get a list of users for the given affiliation
	 * @param affiliation affiliation, not null
	 */
	@Override
	public void requestAffiliationList(final MUCOccupant.Affiliation affiliation) {
		MUCAdminPayload mucPayload = new MUCAdminPayload();
		MUCItem item = new  MUCItem();
		item.affiliation = affiliation;
		mucPayload.addItem(item);
		GenericRequest<MUCAdminPayload> request = new GenericRequest<MUCAdminPayload>(
				IQ.Type.Get, getJID(), mucPayload, iqRouter_);
		request.onResponse.connect(new Slot2<MUCAdminPayload, ErrorPayload>() {
			@Override
			public void call(MUCAdminPayload p1, ErrorPayload p2) {
				handleAffiliationListResponse(p1,p2,affiliation);
			} 
		});
		request.send();
	}

	/**
	 * Send a request for getting form for configuring a room 
	 */
	@Override
	public void requestConfigurationForm() {
		MUCOwnerPayload mucPayload = new MUCOwnerPayload();
		GenericRequest<MUCOwnerPayload> request = new GenericRequest<MUCOwnerPayload>(
				IQ.Type.Get, getJID(), mucPayload, iqRouter_);
		request.onResponse.connect(new Slot2<MUCOwnerPayload, ErrorPayload>() {
			@Override
			public void call(MUCOwnerPayload p1, ErrorPayload p2) {
				handleConfigurationFormReceived(p1,p2);
			} 
		});
		request.send();
	}

	/**
	 * Set the reserved status of room to true.
	 * By default a new room with the default configuration is created. 
	 * The effect of calling this function is to leave the room in reserved state
	 * but not configured so that it can be configured later.
	 */
	@Override
	public void setCreateAsReservedIfNew() {
		createAsReservedIfNew = true;
	}

	/**
	 * Set the password used for entering the room.
	 * @param newPassword password, can be null
	 */
	@Override
	public void setPassword(String newPassword) {
		password = newPassword;
	}

	/**
	 * Get the nick name of the MUC room
	 * @return nick name, can be null
	 */
	private String getOwnNick()  {
		return ownMUCJID.getResource();
	}

	private void handleAffiliationChangeResponse(MUCAdminPayload ref, 
			ErrorPayload error, JID jid, MUCOccupant.Affiliation affiliation) {
		if (error != null) {
			onAffiliationChangeFailed.emit(error, jid, affiliation);
		}
	}

	private void handleAffiliationListResponse(MUCAdminPayload payload, 
			ErrorPayload error, MUCOccupant.Affiliation affiliation) {
		if (error != null) {
			onAffiliationListFailed.emit(error);
		} else {                    
			Vector<JID> jids = new Vector<JID>();
			for (MUCItem item : payload.getItems()) {
				if (item.realJID != null) {
					jids.add(item.realJID);
				}
			}
			onAffiliationListReceived.emit(affiliation, jids);
		}
	}

	private void handleConfigurationFormReceived(MUCOwnerPayload payload, 
			ErrorPayload error) {
		Form form = null;
		if (payload != null) {
			form = payload.getForm();
		}
		if (error != null || form == null) {
			onConfigurationFailed.emit(error);
		} else {
			onConfigurationFormReceived.emit(form);
		}
	}

	private void handleConfigurationResultReceived(
			MUCOwnerPayload payload, ErrorPayload error) {
		if (error != null) {
			onConfigurationFailed.emit(error);
		}
	}

	private void handleCreationConfigResponse(MUCOwnerPayload ref , ErrorPayload error) {
		unlocking = false;
		if (error != null) {
			presenceSender.removeDirectedPresenceReceiver(ownMUCJID, 
					DirectedPresenceSender.SendPresence.AndSendPresence);
			onJoinFailed.emit(error);
		} else {
			onJoinComplete.emit(getOwnNick()); /* Previously, this wasn't needed here, 
			as the presence duplication bug caused an emit elsewhere. */
			isUnlocked_ = true;
			onUnlocked.emit();
		}
	}

	private void handleIncomingPresence(Presence presence) {
		if (!isFromMUC(presence.getFrom())) {
			return;
		}

		MUCUserPayload mucPayload = null;

		MUCUserPayload dummyUserPayload = new MUCUserPayload();
		for (MUCUserPayload payload : presence.getPayloads(dummyUserPayload)) {
			if (!payload.getItems().isEmpty() || !payload.getStatusCodes().isEmpty()) {
				mucPayload = payload;
			}
		}

		// On the first incoming presence, check if our join has succeeded
		// (i.e. we start getting non-error presence from the MUC) or not
		if (!joinSucceeded_) {
			if(presence.getType().equals(Presence.Type.Error)) {
				onJoinFailed.emit(presence.getPayload(new ErrorPayload()));
				return;
			}
			else {
				joinSucceeded_ = true;
				presenceSender.addDirectedPresenceReceiver(ownMUCJID, 
						DirectedPresenceSender.SendPresence.DontSendPresence);
				if ((presenceSender.getLastSentUndirectedPresence() != null) && !(isEqualExceptID(presenceSender.getLastSentUndirectedPresence(), joinRequestPresence_))) {
					// our presence changed between join request and join complete, send current presence to MUC
					Presence latestPresence = new Presence(presenceSender.getLastSentUndirectedPresence());
					latestPresence.setTo(ownMUCJID);
					presenceSender.sendPresence(latestPresence);
				}
			}
		}

		String nick = presence.getFrom().getResource();
		if (nick == null || nick.isEmpty()) {
			return;
		}
		MUCOccupant.Role role = MUCOccupant.Role.NoRole;
		MUCOccupant.Affiliation affiliation= MUCOccupant.Affiliation.NoAffiliation;
		JID realJID = null;
		if (mucPayload != null && mucPayload.getItems().size() > 0) {
			role = mucPayload.getItems().get(0).role != null 
			? mucPayload.getItems().get(0).role : MUCOccupant.Role.NoRole;
			affiliation = mucPayload.getItems().get(0).affiliation != null 
			? mucPayload.getItems().get(0).affiliation : MUCOccupant.Affiliation.NoAffiliation;
			realJID = mucPayload.getItems().get(0).realJID;
		}

		//100 is non-anonymous
		//TODO: 100 may also be specified in a <message/>
		//170 is room logging to http
		//TODO: Nick changes

		if (presence.getType().equals(Presence.Type.Unavailable)) {
			LeavingType type = LeavingType.LeavePart;
			String newNickname = null;            
			if (mucPayload != null) {
				if (mucPayload.getPayload() instanceof MUCDestroyPayload) {
					type = LeavingType.LeaveDestroy;
				} else for (MUCUserPayload.StatusCode status : mucPayload.getStatusCodes()) {
					if (status.code == 307) {
						type = LeavingType.LeaveKick;
					} else if (status.code == 301) {
						type = LeavingType.LeaveBan;
					} else if (status.code == 321) {
						type = LeavingType.LeaveNotMember;
					} else if (status.code == 303) {
						if (mucPayload.getItems().size() == 1) {
							newNickname = mucPayload.getItems().get(0).nick;
						}
					}
				}
			}

			if (newNickname != null) {
				if (occupants.containsKey(nick)) {
					MUCOccupant occupant = occupants.get(nick);
					occupants.remove(nick);
					occupant.setNick(newNickname);
					occupants.put(newNickname, occupant);
					onOccupantNicknameChanged.emit(nick, newNickname);
				}
			}
			else {
				if (presence.getFrom().equals(ownMUCJID)) {
					handleUserLeft(type);
					return;
				} else {
					if (occupants.containsKey(nick)) {
						//TODO: part type
						MUCOccupant occupant = occupants.get(nick);
						occupants.remove(nick);
						onOccupantLeft.emit(occupant, type, "");
					}
				}
			}
		} 
		else if (presence.getType().equals(Presence.Type.Available)) {
			MUCOccupant occupant = new MUCOccupant(nick, role, affiliation);
			boolean isJoin = true;
			if (realJID != null) {
				occupant.setRealJID(realJID);
			}
			if (occupants.containsKey(nick)) {
				isJoin = false;
				MUCOccupant oldOccupant = occupants.get(nick);
				if (!oldOccupant.getRole().equals(role)) {
					onOccupantRoleChanged.emit(nick, occupant, oldOccupant.getRole());
				}
				if (!oldOccupant.getAffiliation().equals(affiliation)) {
					onOccupantAffiliationChanged.emit(nick, affiliation, oldOccupant.getAffiliation());
				}
				occupants.remove(nick);
			}
			occupants.put(nick, occupant);

			if (isJoin) {
				onOccupantJoined.emit(occupant);
			}
			onOccupantPresenceChange.emit(presence);
		}

		if (mucPayload != null && !joinComplete_) {
			boolean isLocked = false;            
			for (MUCUserPayload.StatusCode status : mucPayload.getStatusCodes()) {
				if(status.code == 110) {
					/* Simply knowing this is your presence is enough, 210 doesn't seem to be necessary. */
					joinComplete_ = true;
					if (!ownMUCJID.equals(presence.getFrom())) {
						presenceSender.removeDirectedPresenceReceiver(ownMUCJID, DirectedPresenceSender.SendPresence.DontSendPresence);
						ownMUCJID = presence.getFrom();
						presenceSender.addDirectedPresenceReceiver(ownMUCJID, DirectedPresenceSender.SendPresence.AndSendPresence);
					}
				}
				if (status.code == 201) {
					isLocked = true;                    
					/* Room is created and locked */
					/* Currently deal with this by making an instant room */
					if (!ownMUCJID.equals(presence.getFrom())) {
						presenceSender.removeDirectedPresenceReceiver(ownMUCJID, DirectedPresenceSender.SendPresence.DontSendPresence);
						ownMUCJID = presence.getFrom();
						presenceSender.addDirectedPresenceReceiver(ownMUCJID, DirectedPresenceSender.SendPresence.AndSendPresence);
					}
					if (createAsReservedIfNew) {
						unlocking = true;
						requestConfigurationForm();
					} else {
						// Accept default room configuration and create an instant room http://xmpp.org/extensions/xep-0045.html#createroom-instant
						MUCOwnerPayload mucOwnerPayload = new MUCOwnerPayload();
						presenceSender.addDirectedPresenceReceiver(ownMUCJID, DirectedPresenceSender.SendPresence.DontSendPresence);
						mucOwnerPayload.setPayload(new Form(Form.Type.SUBMIT_TYPE));
						GenericRequest<MUCOwnerPayload> request = new GenericRequest<MUCOwnerPayload>(IQ.Type.Set, 
								getJID(), mucOwnerPayload, iqRouter_);
						request.onResponse.connect(new Slot2<MUCOwnerPayload, ErrorPayload>() {
							@Override
							public void call(MUCOwnerPayload p1,ErrorPayload p2) {
								handleCreationConfigResponse(p1,p2);

							}
						});
						request.send();
					}
				}
			}
			if (joinComplete_ && !isLocked) {
				onJoinComplete.emit(getOwnNick());
			}
			if (!isLocked && !isUnlocked_ && (presence.getFrom().equals(ownMUCJID))) {
				isUnlocked_ = true;
				onUnlocked.emit();
			}
		}
	}

	private void handleOccupantRoleChangeResponse(MUCAdminPayload ref , ErrorPayload error, JID jid, MUCOccupant.Role role) {
		if (error != null) {
			onRoleChangeFailed.emit(error, jid, role);
		}
	}

	/**
	 * This function compares two Presence elements for equality based on to, from, status, show and entity capability information.
	 * @return True if equal; else otherwise.
	 */
	private static boolean isEqualExceptID(final Presence lhs, final Presence rhs) {
		boolean isEqual = false;
		if(lhs == null || rhs == null) {
			isEqual = (lhs == null && rhs == null);
		}
		else if (lhs.getFrom().equals(rhs.getFrom()) && lhs.getTo().equals(rhs.getTo()) && lhs.getStatus().equals(rhs.getStatus()) && lhs.getShow().equals(rhs.getShow())) {
			CapsInfo lhsCaps = lhs.getPayload(new CapsInfo());
			CapsInfo rhsCaps = rhs.getPayload(new CapsInfo());

			if (lhsCaps != null && rhsCaps != null) {
				isEqual = (lhsCaps.equals(rhsCaps));
			}
			else {
				isEqual = (lhsCaps == null && rhsCaps == null);
			}
		}
		return isEqual;
	}

	private void internalJoin(String nick) {
		//TODO: history request
		joinComplete_ = false;
		joinSucceeded_ = false;

		mucRegistry.addMUC(getJID());

		ownMUCJID = new JID(ownMUCJID.getNode(), ownMUCJID.getDomain(), nick);
		Presence joinPresence = (presenceSender.getLastSentUndirectedPresence() != null ? new Presence(presenceSender.getLastSentUndirectedPresence()) : new Presence());
		if(joinPresence.getType() != Presence.Type.Available) {
			throw new RuntimeException("From[" + joinPresence.getFrom() + "] and" +
					" To[" + joinPresence.getTo() + "] is not available");   
		}
		joinPresence.setTo(ownMUCJID);
		MUCPayload mucPayload = new MUCPayload();
		if (joinSince_ != null) {
			mucPayload.setSince(joinSince_);
		}
		if (password != null) {
			mucPayload.setPassword(password);
		}
		joinPresence.addPayload(mucPayload);
		joinRequestPresence_ = joinPresence;
		presenceSender.sendPresence(joinPresence);
	}


	private boolean isFromMUC(final JID j) {
		return ownMUCJID.compare(j, CompareType.WithoutResource) == 0;
	}

	@Override
	public void handleUserLeft(LeavingType type) {
		String resource = ownMUCJID.getResource();
		if (occupants.containsKey(resource)) {
			MUCOccupant me = occupants.get(resource);
			occupants.remove(resource);
			onOccupantLeft.emit(me, type, "");
		}
		occupants.clear();
		joinComplete_ = false;
		joinSucceeded_ = false;
		isUnlocked_ = false;        
		presenceSender.removeDirectedPresenceReceiver(ownMUCJID, 
				DirectedPresenceSender.SendPresence.DontSendPresence);
	}
	
	/**
	 * Disconnect signals for this MUC.
	 * This method should be called when the MUC object is no longer in use
	 * so as to enable the garbage collector to remove this object from used space. 
	 */
	@Override
	public void disconnect() {
		if (scopedConnection_ != null) {
			scopedConnection_.disconnect();
			scopedConnection_ = null;
		}
	}

	protected void finalize() throws Throwable {
		try {
		  	disconnect();
	   	}
	   	finally {
			super.finalize();
	   	}
	}

	//TODO: Invites(direct/mediated)

	//TODO: requesting membership

	//TODO: get member list

	//TODO: request voice

	//TODO: moderator use cases

	//TODO: Admin use cases

	//TODO: Owner use cases

}
