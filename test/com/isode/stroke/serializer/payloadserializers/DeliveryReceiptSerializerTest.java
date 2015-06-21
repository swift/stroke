/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.serializer.payloadserializers;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.isode.stroke.serializer.payloadserializers.DeliveryReceiptRequestSerializer;
import com.isode.stroke.serializer.payloadserializers.DeliveryReceiptSerializer;
import com.isode.stroke.elements.DeliveryReceiptRequest;
import com.isode.stroke.elements.DeliveryReceipt;

public class DeliveryReceiptSerializerTest {

	/**
	* Default Constructor.
	*/
	public DeliveryReceiptSerializerTest() {

	}

	@Test
	public void testSerialize_XEP0184Example3() {
		String expected =	"<request xmlns=\"urn:xmpp:receipts\"/>";

		DeliveryReceiptRequest receipt = new DeliveryReceiptRequest();

		DeliveryReceiptRequestSerializer serializer = new DeliveryReceiptRequestSerializer();
		assertEquals(expected, serializer.serializePayload(receipt));
	}

	@Test
	public void testSerialize_XEP0184Example4() {
		String expected =	"<received id=\"richard2-4.1.247\" xmlns=\"urn:xmpp:receipts\"/>";

		DeliveryReceipt receipt = new DeliveryReceipt("richard2-4.1.247");

		DeliveryReceiptSerializer serializer = new DeliveryReceiptSerializer();
		assertEquals(expected, serializer.serializePayload(receipt));
	}
}