/*
 * Copyright (c) 2011 Tobias Markmann
 * Licensed under the BSD license.
 * See http://www.opensource.org/licenses/bsd-license.php for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.parser.payloadparsers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import com.isode.stroke.elements.DeliveryReceiptRequest;
import com.isode.stroke.elements.DeliveryReceipt;
import com.isode.stroke.parser.payloadparsers.DeliveryReceiptParser;
import com.isode.stroke.parser.payloadparsers.DeliveryReceiptRequestParser;
import com.isode.stroke.parser.payloadparsers.PayloadsParserTester;
import com.isode.stroke.eventloop.DummyEventLoop;

public class DeliveryReceiptParserTest {

	public DeliveryReceiptParserTest() {

	}

	@Test
	public void testParseXEP0184Example3() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse("<request xmlns='urn:xmpp:receipts'/>"));

		DeliveryReceiptRequest request = (DeliveryReceiptRequest)(parser.getPayload());

		assertNotNull(request);
	}

	@Test
	public void testParseXEP0184Example4() {
		DummyEventLoop eventLoop = new DummyEventLoop();
		PayloadsParserTester parser = new PayloadsParserTester(eventLoop);
		assertNotNull(parser.parse("<received xmlns='urn:xmpp:receipts' id='richard2-4.1.247'/>"));

		DeliveryReceipt receipt = (DeliveryReceipt)(parser.getPayload());

		assertEquals("richard2-4.1.247", receipt.getReceivedID());
	}
}