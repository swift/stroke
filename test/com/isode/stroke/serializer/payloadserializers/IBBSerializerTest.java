/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.serializer.payloadserializers;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.isode.stroke.serializer.payloadserializers.IBBSerializer;
import com.isode.stroke.elements.IBB;
import com.isode.stroke.base.ByteArray;

public class IBBSerializerTest {

	/**
	* Default Constructor.
	*/
	public IBBSerializerTest() {

	}

	@Test
	public void testSerialize_data() {
		IBBSerializer testling = new IBBSerializer();
		IBB ibb = new IBB();
		ibb.setAction(IBB.Action.Data);
		ibb.setData(new ByteArray("abcdefgihjklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890\n"));
		ibb.setSequenceNumber(4);
		String expectedResult = "<data seq=\"4\" sid=\"\" xmlns=\"http://jabber.org/protocol/ibb\">" +
					"YWJjZGVmZ2loamtsbW5vcHFyc3R1dnd4eXpBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWjEyMzQ1" +
					"Njc4OTAK" +
					"</data>";
		assertEquals(expectedResult, testling.serialize(ibb));
	}
}