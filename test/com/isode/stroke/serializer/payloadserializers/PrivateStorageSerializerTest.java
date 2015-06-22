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

package com.isode.stroke.serializer.payloadserializers;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.isode.stroke.serializer.payloadserializers.PayloadsSerializer;
import com.isode.stroke.elements.PrivateStorage;
import com.isode.stroke.elements.Storage;
import com.isode.stroke.jid.JID;

public class PrivateStorageSerializerTest {

	/**
	* Default Constructor.
	*/
	public PrivateStorageSerializerTest() {

	}

	@Test
	public void testSerialize() {
		PayloadsSerializer serializer = new PayloadsSerializer();

		PrivateStorage privateStorage = new PrivateStorage();
		Storage storage = new Storage();
		Storage.Room room = new Storage.Room();
		room.name = "Swift";
		room.jid = new JID("swift@rooms.swift.im");
		room.nick = "Alice";
		storage.addRoom(room);
		privateStorage.setPayload(storage);

		assertEquals(
			"<query xmlns=\"jabber:iq:private\">"	
		+		"<storage xmlns=\"storage:bookmarks\">"
		+			"<conference "
		+					"autojoin=\"0\" "
		+					"jid=\"swift@rooms.swift.im\" "
		+					"name=\"Swift\">"
		+				"<nick>Alice</nick>"
		+			"</conference>"
		+		"</storage>"
		+	"</query>", serializer.serialize(privateStorage));
	}
}