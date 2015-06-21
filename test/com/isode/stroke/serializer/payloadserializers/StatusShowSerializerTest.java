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
import com.isode.stroke.serializer.payloadserializers.StatusShowSerializer;
import com.isode.stroke.serializer.PayloadSerializerCollection;
import com.isode.stroke.elements.StatusShow;

public class StatusShowSerializerTest {

	/**
	* Default Constructor.
	*/
	public StatusShowSerializerTest() {

	}

	@Test
	public void testSerialize_Online() {
		StatusShowSerializer testling = new StatusShowSerializer();
		StatusShow statusShow = new StatusShow(StatusShow.Type.Online);
		assertEquals("", testling.serialize(statusShow));
	}

	@Test
	public void testSerialize_Away() {
		StatusShowSerializer testling = new StatusShowSerializer();
		StatusShow statusShow = new StatusShow(StatusShow.Type.Away);
		assertEquals("<show>away</show>", testling.serialize(statusShow));
	}

	@Test
	public void testSerialize_FFC() {
		StatusShowSerializer testling = new StatusShowSerializer();
		StatusShow statusShow = new StatusShow(StatusShow.Type.FFC);
		assertEquals("<show>chat</show>", testling.serialize(statusShow));
	}

	@Test
	public void testSerialize_XA() {
		StatusShowSerializer testling = new StatusShowSerializer();
		StatusShow statusShow = new StatusShow(StatusShow.Type.XA);
		assertEquals("<show>xa</show>", testling.serialize(statusShow));
	}

	@Test
	public void testSerialize_DND() {
		StatusShowSerializer testling = new StatusShowSerializer();
		StatusShow statusShow = new StatusShow(StatusShow.Type.DND);
		assertEquals("<show>dnd</show>", testling.serialize(statusShow));
	}

	@Test
	public void testSerialize_None() {
		StatusShowSerializer testling = new StatusShowSerializer();
		StatusShow statusShow = new StatusShow(StatusShow.Type.None);
		assertEquals("", testling.serialize(statusShow));
	}
}