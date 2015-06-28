/*
 * Copyright (c) 2010 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.chat;

import com.isode.stroke.elements.Message;
import com.isode.stroke.elements.ChatState;
import com.isode.stroke.elements.DiscoInfo;
import com.isode.stroke.disco.EntityCapsProvider;
import com.isode.stroke.client.StanzaChannel;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.jid.JID;

public class ChatStateNotifier {

	private StanzaChannel stanzaChannel_;
	private EntityCapsProvider entityCapsManager_;
	private JID contact_ = new JID();
	private boolean contactHas85Caps_;
	private boolean contactHasSentActive_;
	private boolean userIsTyping_;
	private boolean contactIsOnline_;
	private SignalConnection onCapsChangedConnection;

	public ChatStateNotifier(StanzaChannel stanzaChannel, JID contact, EntityCapsProvider entityCapsManager) {
		this.stanzaChannel_ = stanzaChannel;
		this.contact_ = contact;
		this.entityCapsManager_ = entityCapsManager;
		setContact(contact);
		onCapsChangedConnection = entityCapsManager_.onCapsChanged.connect(new Slot1<JID>() {

			public void call(JID j1) {
				handleCapsChanged(j1);
			}
		});
	}

	private boolean contactShouldReceiveStates() {
		/* So, yes, the XEP says to look at caps, but it also says that once you've
			 heard from the contact, the active state overrides this.
			 *HOWEVER* it says that the MUST NOT send csn if you haven't received
			 active is OPTIONAL behaviour for if you haven't got caps.*/
		return contactIsOnline_ && (contactHasSentActive_ || contactHas85Caps_);
	}

	private void changeState(ChatState.ChatStateType state) {
		Message message = new Message();
		message.setTo(contact_);
		message.addPayload(new ChatState(state));
		stanzaChannel_.sendMessage(message);
	}

	private void handleCapsChanged(JID jid) {
		if (jid.equals(contact_)) {
			DiscoInfo caps = entityCapsManager_.getCaps(contact_);
			boolean hasCSN = (caps != null) && (caps.hasFeature(DiscoInfo.ChatStatesFeature));
			contactHas85Caps_ = hasCSN;
		}
	}

	public void setContact(JID contact) {
		contactHasSentActive_ = false;
		userIsTyping_ = false;
		contactIsOnline_ = false;
		contact_ = contact;
		handleCapsChanged(contact_);
	}

	public void addChatStateRequest(Message message) {
		if (contactShouldReceiveStates()) {
			message.addPayload(new ChatState(ChatState.ChatStateType.Active));
		}
	}

	public void setUserIsTyping() {
		boolean should = contactShouldReceiveStates();
		if (should && !userIsTyping_) {
			userIsTyping_ = true;
			changeState(ChatState.ChatStateType.Composing);
		}
	}

	public void userSentMessage() {
		userIsTyping_ = false;
	}

	public void userCancelledNewMessage() {
		if (userIsTyping_) {
			userIsTyping_ = false;
			changeState(ChatState.ChatStateType.Active);
		}
	}

	public void receivedMessageFromContact(boolean hasActiveElement) {
		contactHasSentActive_ = hasActiveElement;
	}

	public void setContactIsOnline(boolean online) {
		contactIsOnline_ = online;
	}
}
