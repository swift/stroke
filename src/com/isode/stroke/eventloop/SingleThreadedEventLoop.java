/*
 * Copyright (c) 2010 Soren Dreijer
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.eventloop;

import com.isode.stroke.eventloop.EventLoop;
import java.util.Vector;
import java.util.Collection;
import java.util.ArrayList;

// DESCRIPTION:
//
// All interaction with Swiften should happen on the same thread, such as the main GUI thread,
// since the library isn't thread-safe.
// For applications that don't have a main loop, such as WPF and MFC applications, we need a
// different approach to process events from Swiften.
//
// The SingleThreadedEventLoop class implements an event loop that can be used from such applications.
//
// USAGE:
//  
// Spawn a new thread in the desired framework and call SingleThreadedEventLoop::waitForEvents(). The method 
// blocks until a new event has arrived at which time it'll return, or until the wait is canceled
// at which time it throws EventLoopCanceledException. 
//
// When a new event has arrived and SingleThreadedEventLoop::waitForEvents() returns, the caller should then
// call SingleThreadedEventLoop::handleEvents() on the main GUI thread. For WPF applications, for instance, 
// the Dispatcher class can be used to execute the call on the GUI thread.
//
public class SingleThreadedEventLoop extends EventLoop {

	public class EventLoopCanceledException extends Exception {
		public EventLoopCanceledException(String message) {
			super(message);
		}
	}

	private boolean shouldShutDown_;
	private Vector<Event> events_ = new Vector<Event>();
	private Object eventsMutex_ = new Object();

	public SingleThreadedEventLoop() {
		shouldShutDown_ = false;
	}

	/**
	 * Blocks while waiting for new events and returns when new events are available.
	 * @throws EventLoopCanceledException when the wait is canceled.
	 */
	public void waitForEvents() throws EventLoopCanceledException {
		synchronized(eventsMutex_) {
			while (events_.isEmpty() && !shouldShutDown_) {
				try {
					eventsMutex_.wait();
				} catch (InterruptedException e) {

				}
			}
		}
		if (shouldShutDown_) {
			throw new EventLoopCanceledException("");
		}
	}

	public void handleEvents() {
		// Make a copy of the list of events so we don't block any threads that post 
		// events while we process them.
		Vector<Event> events = new Vector<Event>();
		synchronized(eventsMutex_) {
			swapCollectionContents(events, events_);
		}

		// Loop through all the events and handle them
		for(Event event : events) {
			handleEvent(event);
		}
	}

	static <T> void swapCollectionContents(Collection<T> coll1, Collection<T> coll2) {
		Collection<T> temp = new ArrayList<T>(coll1);
		coll1.clear();
		coll1.addAll(coll2);
		coll2.clear();
		coll2.addAll(temp);
	}

	public void stop() {
		synchronized(eventsMutex_) {
			shouldShutDown_ = true;
			eventsMutex_.notifyAll();
		}
	}

	public void post(final Event event) {
		synchronized(eventsMutex_) {
			events_.add(event);
			eventsMutex_.notifyAll();
		}
	}
}