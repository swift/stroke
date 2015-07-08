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
import com.isode.stroke.queries.IQHandler;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.queries.DummyIQChannel;
import com.isode.stroke.jid.JID;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.elements.ErrorPayload;

public class IQRouterTest {

	private DummyIQChannel channel_;

	private class DummyIQHandler implements IQHandler {

		public DummyIQHandler(boolean handle, IQRouter router) {
			this.handle = handle;
			this.router = router;
			this.called = 0;
			router.addHandler(this);
		}

		public void delete() {
			router.removeHandler(this);
		}

		@Override
		public boolean handleIQ(IQ iq) {
			called++;
			return handle;
		}

		public boolean handle;
		public IQRouter router;
		public int called;
	}

	private class RemovingIQHandler implements IQHandler {

		public RemovingIQHandler(IQRouter router) {
			this.router = router;
			this.called = 0;
			router.addHandler(this);
		}

		@Override
		public boolean handleIQ(IQ iq) {
			called++;
			router.removeHandler(this);
			return false;
		}

		public IQRouter router;
		public int called;
	}

	@Before
	public void setUp() {
		channel_ = new DummyIQChannel();
	}

	@Test
	public void testRemoveHandler() {
		IQRouter testling = new IQRouter(channel_);
		DummyIQHandler handler1 = new DummyIQHandler(true, testling);
		DummyIQHandler handler2 = new DummyIQHandler(true, testling);
		testling.removeHandler(handler1);

		channel_.onIQReceived.emit(new IQ());

		assertEquals(0, handler1.called);
		assertEquals(1, handler2.called);
	}

	@Test
	public void testRemoveHandler_AfterHandleIQ() {
		IQRouter testling = new IQRouter(channel_);
		DummyIQHandler handler2 = new DummyIQHandler(true, testling);
		DummyIQHandler handler1 = new DummyIQHandler(true, testling);

		channel_.onIQReceived.emit(new IQ());
		testling.removeHandler(handler1);
		channel_.onIQReceived.emit(new IQ());

		assertEquals(1, handler1.called);
		assertEquals(1, handler2.called);
	}

	@Test
	public void testHandleIQ_SuccesfulHandlerFirst() {
		IQRouter testling = new IQRouter(channel_);
		DummyIQHandler handler2 = new DummyIQHandler(false, testling);
		DummyIQHandler handler1 = new DummyIQHandler(true, testling);

		channel_.onIQReceived.emit(new IQ());

		assertEquals(1, handler1.called);
		assertEquals(0, handler2.called);
		assertEquals(0, channel_.iqs_.size());
	}

	@Test
	public void testHandleIQ_SuccesfulHandlerLast() {
		IQRouter testling = new IQRouter(channel_);
		DummyIQHandler handler2 = new DummyIQHandler(true, testling);
		DummyIQHandler handler1 = new DummyIQHandler(false, testling);

		channel_.onIQReceived.emit(new IQ());

		assertEquals(1, handler1.called);
		assertEquals(1, handler2.called);
		assertEquals(0, channel_.iqs_.size());
	}

	@Test
	public void testHandleIQ_NoSuccesfulHandler() {
		IQRouter testling = new IQRouter(channel_);
		DummyIQHandler handler = new DummyIQHandler(false, testling);

		channel_.onIQReceived.emit(new IQ());

		assertEquals(1, channel_.iqs_.size());
		assertNotNull(channel_.iqs_.get(0).getPayload(new ErrorPayload()));
	}

	@Test
	public void testHandleIQ_HandlerRemovedDuringHandle() {
		IQRouter testling = new IQRouter(channel_);
		DummyIQHandler handler2 = new DummyIQHandler(true, testling);
		RemovingIQHandler handler1 = new RemovingIQHandler(testling);

		channel_.onIQReceived.emit(new IQ());
		channel_.onIQReceived.emit(new IQ());

		assertEquals(1, handler1.called);
		assertEquals(2, handler2.called);
	}

	@Test
	public void testSendIQ_WithFrom() {
		IQRouter testling = new IQRouter(channel_);
		testling.setFrom(new JID("foo@bar.com/baz"));

		testling.sendIQ(new IQ());

		assertEquals(new JID("foo@bar.com/baz"), channel_.iqs_.get(0).getFrom());
	}

	@Test
	public void testSendIQ_WithoutFrom() {
		IQRouter testling = new IQRouter(channel_);

		testling.sendIQ(new IQ());

		assertEquals(new JID(), channel_.iqs_.get(0).getFrom());
	}

	@Test
	public void testHandleIQ_WithFrom() {
		IQRouter testling = new IQRouter(channel_);
		testling.setFrom(new JID("foo@bar.com/baz"));
		DummyIQHandler handler = new DummyIQHandler(false, testling);

		channel_.onIQReceived.emit(new IQ());

		assertEquals(new JID("foo@bar.com/baz"), channel_.iqs_.get(0).getFrom());
	}
}