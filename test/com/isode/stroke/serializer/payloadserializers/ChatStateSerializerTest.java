package com.isode.stroke.serializer.payloadserializers;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;
import src.com.isode.stroke.elements.ChatState;
import src.com.isode.stroke.serializer.payloadserializers.ChatStateSerializer;

public class ChatStateSerializerTest {

	ChatStateSerializer testling = new ChatStateSerializer();
	
	@Test
	public void testSerialize() {
		testGoneState();
		testComposingState();
		testPausedState();
		testInactiveState();
		testActiveState();
	}

	@Test
	void testGoneState() {
		ChatState priority = new ChatState(ChatState.ChatStateType.Gone);
		String expected = "<gone xmlns=\"http://jabber.org/protocol/chatstates\"/>";
		assertEquals(expected, testling.serialize(priority));
	}

	@Test
	void testComposingState() {
		ChatState priority = new ChatState(ChatState.ChatStateType.Composing);
		String expected = "<composing xmlns=\"http://jabber.org/protocol/chatstates\"/>";
		assertEquals(expected, testling.serialize(priority));
	}

	@Test
	void testPausedState() {
		ChatState priority = new ChatState(ChatState.ChatStateType.Paused);
		String expected = "<paused xmlns=\"http://jabber.org/protocol/chatstates\"/>";
		assertEquals(expected, testling.serialize(priority));
	}

	@Test
	void testInactiveState() {
		ChatState priority = new ChatState(ChatState.ChatStateType.Inactive);
		String expected = "<inactive xmlns=\"http://jabber.org/protocol/chatstates\"/>";
		assertEquals(expected, testling.serialize(priority));
	}

	@Test
	void testActiveState() {
		ChatState priority = new ChatState(ChatState.ChatStateType.Active);
		String expected = "<active xmlns=\"http://jabber.org/protocol/chatstates\"/>";
		assertEquals(expected, testling.serialize(priority));	
	}
}