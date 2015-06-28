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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Before;
import com.isode.stroke.elements.ChatState;
import com.isode.stroke.elements.DiscoInfo;
import com.isode.stroke.elements.Stanza;
import com.isode.stroke.elements.Message;
import com.isode.stroke.jid.JID;
import com.isode.stroke.chat.ChatStateNotifier;
import com.isode.stroke.client.DummyStanzaChannel;
import com.isode.stroke.disco.DummyEntityCapsProvider;

public class ChatStateNotifierTest {

	private DummyStanzaChannel stanzaChannel;
	private DummyEntityCapsProvider entityCapsProvider;
	private ChatStateNotifier notifier_;

	public ChatStateNotifierTest() {

	}

	@Before
	public void setUp() {
		stanzaChannel = new DummyStanzaChannel();
		stanzaChannel.setAvailable(true);
		entityCapsProvider = new DummyEntityCapsProvider();
		notifier_ = new ChatStateNotifier(stanzaChannel, new JID("foo@bar.com/baz"), entityCapsProvider);
		notifier_.setContactIsOnline(true);
	}

	private void setContactHas85Caps() {
		DiscoInfo caps = new DiscoInfo();
		caps.addFeature(DiscoInfo.ChatStatesFeature);
		entityCapsProvider.caps.put(new JID("foo@bar.com/baz"), caps);
		entityCapsProvider.onCapsChanged.emit(new JID("foo@bar.com/baz"));
	}

	private int getComposingCount() {
		int result = 0;
		for(Stanza stanza : stanzaChannel.sentStanzas) {
			if (stanza.getPayload(new ChatState()) != null && stanza.getPayload(new ChatState()).getChatState() == ChatState.ChatStateType.Composing) {
				result++;
			}
		}
		return result;
	}

	private int getActiveCount() {
		int result = 0;
		for(Stanza stanza : stanzaChannel.sentStanzas) {
			if (stanza.getPayload(new ChatState()) != null && stanza.getPayload(new ChatState()).getChatState() == ChatState.ChatStateType.Active) {
				result++;
			}
		}
		return result;
	}

	@Test
	public void testStartTypingReply_CapsNotIncluded() {
		notifier_.setUserIsTyping();
		assertEquals(0, getComposingCount());
	}


	@Test
	public void testSendTwoMessages() {
		setContactHas85Caps();
		notifier_.setUserIsTyping();
		notifier_.userSentMessage();
		notifier_.setUserIsTyping();
		notifier_.userSentMessage();
		assertEquals(2, getComposingCount());
	}


	@Test
	public void testCancelledNewMessage() {
		setContactHas85Caps();
		notifier_.setUserIsTyping();
		notifier_.userCancelledNewMessage();
		assertEquals(1, getComposingCount());
		assertEquals(1, getActiveCount());
		assertEquals(ChatState.ChatStateType.Active, stanzaChannel.sentStanzas.get(stanzaChannel.sentStanzas.size()-1).getPayload(new ChatState()).getChatState());
	}

	@Test
	public void testContactShouldReceiveStates_CapsOnly() {
		setContactHas85Caps();
		Message message = new Message();
		notifier_.addChatStateRequest(message);
		assertNotNull(message.getPayload(new ChatState()));
		assertEquals(ChatState.ChatStateType.Active, message.getPayload(new ChatState()).getChatState());
	}

	@Test
	public void testContactShouldReceiveStates_CapsNorActive() {
		Message message = new Message();
		notifier_.addChatStateRequest(message);
		assertNull(message.getPayload(new ChatState()));
	}

	@Test
	public void testContactShouldReceiveStates_ActiveOverrideOn() {
		notifier_.receivedMessageFromContact(true);
		Message message = new Message();
		notifier_.addChatStateRequest(message);
		assertNotNull(message.getPayload(new ChatState()));
		assertEquals(ChatState.ChatStateType.Active, message.getPayload(new ChatState()).getChatState());
	}

	@Test
	public void testContactShouldReceiveStates_ActiveOverrideOff() {
		setContactHas85Caps();
		notifier_.receivedMessageFromContact(false);
		/* I originally read the MUST NOT send after receiving without Active and
		 * thought this should check for false, but I later found it was OPTIONAL
		 * (MAY) behaviour only for if you didn't receive caps.
		 */
		Message message = new Message();
		notifier_.addChatStateRequest(message);
		assertNotNull(message.getPayload(new ChatState()));
		assertEquals(ChatState.ChatStateType.Active, message.getPayload(new ChatState()).getChatState());
	}


	@Test
	public void testStartTypingReply_CapsIncluded() {
		setContactHas85Caps();
		notifier_.setUserIsTyping();
		assertEquals(1, getComposingCount());
	}

	@Test
	public void testContinueTypingReply_CapsIncluded() {
		setContactHas85Caps();
		notifier_.setUserIsTyping();
		notifier_.setUserIsTyping();
		notifier_.setUserIsTyping();
		assertEquals(1, getComposingCount());
		notifier_.userSentMessage();
		notifier_.setUserIsTyping();
		assertEquals(2, getComposingCount());
	}

	@Test
	public void testTypeReplies_WentOffline() {
		setContactHas85Caps();
		notifier_.setUserIsTyping();
		assertEquals(1, getComposingCount());
		notifier_.setContactIsOnline(false);
		notifier_.userSentMessage();
		notifier_.setUserIsTyping();
		assertEquals(1, getComposingCount());
	}
}