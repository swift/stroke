/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.serializer.payloadserializers;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.isode.stroke.serializer.payloadserializers.BytestreamsSerializer;
import com.isode.stroke.elements.Bytestreams;
import com.isode.stroke.jid.JID;

public class BytestreamsSerializerTest {

	public BytestreamsSerializerTest() {

	}

	@Test
	public void testSerialize_withoutUsedStreamHost_singleStreamHost() {
		BytestreamsSerializer testling = new BytestreamsSerializer();
		Bytestreams byteStreams = new Bytestreams();
		byteStreams.addStreamHost(byteStreams.new StreamHost("blah.xyz.edu", new JID("user1@bar.com/bla"), 445));
		byteStreams.setStreamID("hello");
		String expectedResult = "<query sid=\"hello\" xmlns=\"http://jabber.org/protocol/bytestreams\">" +
								"<streamhost host=\"blah.xyz.edu\" jid=\"user1@bar.com/bla\" port=\"445\"/></query>";
		assertEquals(expectedResult, testling.serialize(byteStreams));
	}

	@Test
	public void testSerialize_withoutUsedStreamHost_doubleStreamHost() {
		BytestreamsSerializer testling = new BytestreamsSerializer();
		Bytestreams byteStreams = new Bytestreams();
		byteStreams.addStreamHost(byteStreams.new StreamHost("blah.xyz.edu", new JID("user1@bar.com/bla"), 445));
		byteStreams.addStreamHost(byteStreams.new StreamHost("bal.zyx.ude", new JID("user1@baz.com/bal"), 449));
		byteStreams.setStreamID("hello");
		String expectedResult = "<query sid=\"hello\" xmlns=\"http://jabber.org/protocol/bytestreams\">" +
								"<streamhost host=\"blah.xyz.edu\" jid=\"user1@bar.com/bla\" port=\"445\"/>" +
								"<streamhost host=\"bal.zyx.ude\" jid=\"user1@baz.com/bal\" port=\"449\"/>" +
								"</query>";
		assertEquals(expectedResult, testling.serialize(byteStreams));
	}

	@Test
	public void testSerialize_withUsedStreamHost() {
		BytestreamsSerializer testling = new BytestreamsSerializer();
		Bytestreams byteStreams = new Bytestreams();
		byteStreams.addStreamHost(byteStreams.new StreamHost("blah.xyz.edu", new JID("user1@bar.com/bla"), 445));
		byteStreams.setUsedStreamHost(new JID("user1@baz.com/bal"));
		byteStreams.setStreamID("hello");
		String expectedResult = "<query sid=\"hello\" xmlns=\"http://jabber.org/protocol/bytestreams\">" +
								"<streamhost host=\"blah.xyz.edu\" jid=\"user1@bar.com/bla\" port=\"445\"/>" +
								"<streamhost-used jid=\"user1@baz.com/bal\"/>" +
								"</query>";
		assertEquals(expectedResult, testling.serialize(byteStreams));
	}
}