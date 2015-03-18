/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.serializer.payloadserializers;

import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;
import com.isode.stroke.elements.ChatState;
import com.isode.stroke.serializer.payloadserializers.ChatStateSerializer;

public class ChatStateSerializerTest {

	ChatStateSerializer testling = new ChatStateSerializer();

	@Test
	void testSerialize_ActiveState() {
		ChatState priority = new ChatState(ChatState.ChatStateType.Active);
		String expected = "<active xmlns=\"http://jabber.org/protocol/chatstates\"/>";
		assertEquals(expected, testling.serialize(priority));	
	}

	@Test
	void testSerialize_GoneState() {
		ChatState priority = new ChatState(ChatState.ChatStateType.Gone);
		String expected = "<gone xmlns=\"http://jabber.org/protocol/chatstates\"/>";
		assertEquals(expected, testling.serialize(priority));
	}

	@Test
	void testSerialize_ComposingState() {
		ChatState priority = new ChatState(ChatState.ChatStateType.Composing);
		String expected = "<composing xmlns=\"http://jabber.org/protocol/chatstates\"/>";
		assertEquals(expected, testling.serialize(priority));
	}

	@Test
	void testSerialize_PausedState() {
		ChatState priority = new ChatState(ChatState.ChatStateType.Paused);
		String expected = "<paused xmlns=\"http://jabber.org/protocol/chatstates\"/>";
		assertEquals(expected, testling.serialize(priority));
	}

	@Test
	void testSerialize_InactiveState() {
		ChatState priority = new ChatState(ChatState.ChatStateType.Inactive);
		String expected = "<inactive xmlns=\"http://jabber.org/protocol/chatstates\"/>";
		assertEquals(expected, testling.serialize(priority));
	}
}