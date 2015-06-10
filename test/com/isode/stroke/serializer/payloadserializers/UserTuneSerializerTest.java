/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.serializer.payloadserializers;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.isode.stroke.serializer.payloadserializers.UserTuneSerializer;
import com.isode.stroke.serializer.PayloadSerializerCollection;
import com.isode.stroke.elements.UserTune;

public class UserTuneSerializerTest {

	/**
	* Default Constructor.
	*/
	public UserTuneSerializerTest() {

	}

	@Test
	public void testSerialize_withAllVariablesSet() {
		PayloadSerializerCollection serializerCollection = new PayloadSerializerCollection();
		UserTuneSerializer testling = new UserTuneSerializer(serializerCollection);
		UserTune userTune = new UserTune();
		userTune.setRating(5);
		userTune.setTitle("Minion");
		userTune.setTrack("Yellow");
		userTune.setArtist("Ice");
		userTune.setURI("Fire");
		userTune.setSource("Origin");
		userTune.setLength(226);
		String expectedResult = "<tune xmlns=\"http://jabber.org/protocol/tune\">" +
								"<rating>5</rating><title>Minion</title><track>Yellow</track><artist>Ice</artist><uri>Fire</uri><source>Origin</source><length>226</length></tune>";
		assertEquals(expectedResult, testling.serialize(userTune));
	}

	@Test
	public void testSerialize_withSomeVariablesSet() {
		PayloadSerializerCollection serializerCollection = new PayloadSerializerCollection();
		UserTuneSerializer testling = new UserTuneSerializer(serializerCollection);
		UserTune userTune = new UserTune();
		userTune.setTitle("Minion");
		userTune.setTrack("Yellow");
		userTune.setArtist("Ice");
		userTune.setSource("Origin");
		userTune.setLength(226);
		String expectedResult = "<tune xmlns=\"http://jabber.org/protocol/tune\">" +
								"<title>Minion</title><track>Yellow</track><artist>Ice</artist><source>Origin</source><length>226</length></tune>";
		assertEquals(expectedResult, testling.serialize(userTune));
	}
}