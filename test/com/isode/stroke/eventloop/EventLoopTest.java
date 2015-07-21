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

package com.isode.stroke.eventloop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Before;
import com.isode.stroke.eventloop.EventOwner;
import com.isode.stroke.eventloop.SimpleEventLoop;
import com.isode.stroke.eventloop.DummyEventLoop;
import java.util.Vector;

public class EventLoopTest {

	private Vector<Integer> events_ = new Vector<Integer>();

	private class MyEventOwner implements EventOwner {};

	private void logEvent(int i) {
		events_.add(i);
	}

	private void runEventLoop(DummyEventLoop loop, MyEventOwner eventOwner) {
		loop.processEvents();
		assertEquals(0, events_.size());
		loop.postEvent(new Event.Callback() {
			@Override
			public void run() {
				logEvent(1);
			}
		}, eventOwner);
	}

	@Before
	public void setUp() {
		events_.clear();
	}

	@Test
	public void testPost() {
		SimpleEventLoop testling = new SimpleEventLoop();

		testling.postEvent(new Event.Callback() {
			@Override
			public void run() {
				logEvent(1);
			}
		});
		testling.postEvent(new Event.Callback() {
			@Override
			public void run() {
				logEvent(2);
			}
		});
		testling.stop();
		testling.run();

		assertEquals(Integer.valueOf(1), events_.get(0));
		assertEquals(Integer.valueOf(2), events_.get(1));
	}

	@Test
	public void testRemove() {
		SimpleEventLoop testling = new SimpleEventLoop();
		MyEventOwner eventOwner1 = new MyEventOwner();
		MyEventOwner eventOwner2 = new MyEventOwner();

		testling.postEvent(new Event.Callback() {
			@Override
			public void run() {
				logEvent(1);
			}
		}, eventOwner1);
		testling.postEvent(new Event.Callback() {
			@Override
			public void run() {
				logEvent(2);
			}
		}, eventOwner2);
		testling.postEvent(new Event.Callback() {
			@Override
			public void run() {
				logEvent(3);
			}
		}, eventOwner1);
		testling.postEvent(new Event.Callback() {
			@Override
			public void run() {
				logEvent(4);
			}
		}, eventOwner2);
		testling.removeEventsFromOwner(eventOwner2);
		testling.stop();
		testling.run();

		assertEquals(2, events_.size());
		assertEquals(Integer.valueOf(1), events_.get(0));
		assertEquals(Integer.valueOf(3), events_.get(1));
	}

	@Test
	public void testHandleEvent_Recursive() {
		final DummyEventLoop testling = new DummyEventLoop();
		final MyEventOwner eventOwner = new MyEventOwner();

		testling.postEvent(new Event.Callback() {
			@Override
			public void run() {
				runEventLoop(testling, eventOwner);
			}
		}, eventOwner);
		testling.postEvent(new Event.Callback() {
			@Override
			public void run() {
				logEvent(0);
			}
		}, eventOwner);
		testling.processEvents();

		assertEquals(2, events_.size());
		assertEquals(Integer.valueOf(0), events_.get(0));
		assertEquals(Integer.valueOf(1), events_.get(1));
	}
}