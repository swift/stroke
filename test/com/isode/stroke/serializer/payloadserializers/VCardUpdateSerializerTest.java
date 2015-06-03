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
import org.junit.BeforeClass;
import org.junit.Test;
import com.isode.stroke.elements.VCardUpdate;
import com.isode.stroke.serializer.payloadserializers.VCardUpdateSerializer;

public class VCardUpdateSerializerTest {

	@Test
	public void testSerialize() {
		VCardUpdateSerializer testling = new VCardUpdateSerializer();
		VCardUpdate update = new VCardUpdate();
		update.setPhotoHash("sha1-hash-of-image");
		String expectedResult = "<x xmlns=\"vcard-temp:x:update\"><photo>sha1-hash-of-image</photo></x>";
		assertEquals(expectedResult, testling.serialize(update));
	}
}