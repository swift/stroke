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

package com.isode.stroke.queries;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Before;
import com.isode.stroke.queries.Responder;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.queries.DummyIQChannel;
import com.isode.stroke.jid.JID;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.elements.SoftwareVersion;
import java.util.Vector;

public class ResponderTest {

	private IQRouter router_;
	private DummyIQChannel channel_;
	private SoftwareVersion payload_;

	private class MyResponder extends Responder<SoftwareVersion> {

		public MyResponder(IQRouter router) {
			super(new SoftwareVersion(), router);
			getRequestResponse_ = true;
			setRequestResponse_ = true;
		}

		public boolean handleGetRequest(final JID from, final JID to, final String id, SoftwareVersion payload) {
			assertEquals(new JID("foo@bar.com/baz"), from);
			assertEquals("myid", id);
			getPayloads_.add(payload);
			return getRequestResponse_;
		}

		public boolean handleSetRequest(final JID from, final JID to, final String id, SoftwareVersion payload) {
			assertEquals(new JID("foo@bar.com/baz"), from);
			assertEquals("myid", id);
			setPayloads_.add(payload);
			return setRequestResponse_;
		}

		public boolean getRequestResponse_;
		public boolean setRequestResponse_;
		public Vector<SoftwareVersion> getPayloads_ = new Vector<SoftwareVersion>();
		public Vector<SoftwareVersion> setPayloads_ = new Vector<SoftwareVersion>();
	}

	private IQ createRequest(IQ.Type type) {
		IQ iq = new IQ(type);
		iq.addPayload(payload_);
		iq.setID("myid");
		iq.setFrom(new JID("foo@bar.com/baz"));
		return iq;
	}

	@Before
	public void setUp() {
		channel_ = new DummyIQChannel();
		router_ = new IQRouter(channel_);
		payload_ = new SoftwareVersion("foo");
	}

	@Test
	public void testConstructor() {
		MyResponder testling = new MyResponder(router_);

		channel_.onIQReceived.emit(createRequest(IQ.Type.Set));

		assertEquals(0, testling.setPayloads_.size());
	}

	@Test
	public void testStart() {
		MyResponder testling = new MyResponder(router_);

		testling.start();
		channel_.onIQReceived.emit(createRequest(IQ.Type.Set));

		assertEquals(1, testling.setPayloads_.size());
	}

	@Test
	public void testStop() {
		MyResponder testling = new MyResponder(router_);

		testling.start();
		testling.stop();
		channel_.onIQReceived.emit(createRequest(IQ.Type.Set));

		assertEquals(0, testling.setPayloads_.size());
	}

	@Test
	public void testHandleIQ_Set() {
		MyResponder testling = new MyResponder(router_);

		assertTrue(((IQHandler)testling).handleIQ(createRequest(IQ.Type.Set)));

		assertEquals(1, testling.setPayloads_.size());
		assertEquals(payload_, testling.setPayloads_.get(0));
		assertEquals(0, testling.getPayloads_.size());
	}

	@Test
	public void testHandleIQ_Get() {
		MyResponder testling = new MyResponder(router_);

		assertTrue(((IQHandler)testling).handleIQ(createRequest(IQ.Type.Get)));

		assertEquals(1, testling.getPayloads_.size());
		assertEquals(0, testling.setPayloads_.size());
		assertEquals(payload_, testling.getPayloads_.get(0));
	}

	@Test
	public void testHandleIQ_Error() {
		MyResponder testling = new MyResponder(router_);

		assertFalse(((IQHandler)testling).handleIQ(createRequest(IQ.Type.Error)));

		assertEquals(0, testling.getPayloads_.size());
		assertEquals(0, testling.setPayloads_.size());
	}

	@Test
	public void testHandleIQ_Result() {
		MyResponder testling = new MyResponder(router_);

		assertFalse(((IQHandler)testling).handleIQ(createRequest(IQ.Type.Result)));

		assertEquals(0, testling.getPayloads_.size());
		assertEquals(0, testling.setPayloads_.size());
	}

	@Test
	public void testHandleIQ_NoPayload() {
		MyResponder testling = new MyResponder(router_);

		assertFalse(((IQHandler)testling).handleIQ(new IQ(IQ.Type.Get)));

		assertEquals(0, testling.getPayloads_.size());
		assertEquals(0, testling.setPayloads_.size());
	}
}
