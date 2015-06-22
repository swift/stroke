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
import com.isode.stroke.elements.Storage;
import com.isode.stroke.jid.JID;

public class StorageSerializerTest {

	/**
	* Default Constructor.
	*/
	public StorageSerializerTest() {

	}

	@Test
	public void testSerialize() {
		PayloadsSerializer serializer = new PayloadsSerializer();
		Storage storage = new Storage();
		Storage.Room room = new Storage.Room();
		room.name = "Council of Oberon";
		room.autoJoin = true;
		room.jid = new JID("council@conference.underhill.org");
		room.nick = "Puck";
		room.password = "MyPass";
		storage.addRoom(room);
		Storage.URL url = new Storage.URL();
		url.name = "Complete Works of Shakespeare";
		url.url = "http://the-tech.mit.edu/Shakespeare/";
		storage.addURL(url);

		assertEquals(
			"<storage xmlns=\"storage:bookmarks\">"
		+		"<conference "
		+				"autojoin=\"1\" "
		+				"jid=\"council@conference.underhill.org\" "
		+				"name=\"Council of Oberon\">"
		+			"<nick>Puck</nick>"
		+			"<password>MyPass</password>"
		+		"</conference>"
		+		"<url name=\"Complete Works of Shakespeare\" url=\"http://the-tech.mit.edu/Shakespeare/\"/>"
		+	"</storage>", serializer.serialize(storage));
	}

	@Test
	public void testSerialize_NoNickOrPassword() {
		PayloadsSerializer serializer = new PayloadsSerializer();
		Storage storage = new Storage();
		Storage.Room room = new Storage.Room();
		room.name = "Council of Oberon";
		room.autoJoin = true;
		room.jid = new JID("council@conference.underhill.org");
		storage.addRoom(room);

		assertEquals(
			"<storage xmlns=\"storage:bookmarks\">"
		+		"<conference "
		+				"autojoin=\"1\" "
		+				"jid=\"council@conference.underhill.org\" "
		+				"name=\"Council of Oberon\"/>"
		+	"</storage>", serializer.serialize(storage));
	}
}