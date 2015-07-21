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
import com.isode.stroke.eventloop.SimpleEventLoop;

public class SimpleEventLoopTest {

	private void runIncrementingThread(SimpleEventLoop loop) {
		for (int i = 0; i < 10; ++i) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {

			}
			loop.postEvent(new Event.Callback() {
				@Override
				public void run() {
					incrementCounter();
				}
			});
		}
		loop.stop();
	}

	private void incrementCounter() {
		counter_++;
	}

	private void incrementCounterAndStop(SimpleEventLoop loop) {
		counter_++;
		loop.stop();
	}

	private int counter_;

	@Before
	public void setUp() {
		counter_ = 0;
	}

	@Test
	// FIXME: Temporarily disabling run, because it generates a "vector 
	// iterator not incrementable" on XP
	public void testRun() {
		final SimpleEventLoop testling = new SimpleEventLoop();
		Thread d = new Thread(new Runnable() {
			@Override
			public void run() {
				runIncrementingThread(testling);
			}
		});
		d.start();
		testling.run();

		assertEquals(10, counter_);
	}

	@Test
	public void testPostFromMainThread() {
		final SimpleEventLoop testling = new SimpleEventLoop();
		testling.postEvent(new Event.Callback() {
			@Override
			public void run() {
				incrementCounterAndStop(testling);
			}
		});
		testling.run();

		assertEquals(1, counter_);
	}
}