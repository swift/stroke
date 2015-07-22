/*
* Copyright (c) 2014, Isode Limited, London, England.
* All rights reserved.
*/
/*
 * Copyright (c) 2010 Remko Tronçon
 * Licensed under the GNU General Public License v3.
 * See Documentation/Licenses/GPLv3.txt for more information.
 */

package com.isode.stroke.eventloop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SimpleEventLoop extends EventLoop {

    private boolean isRunning_;
    private final List<Event> events_ = new ArrayList<Event>();
    private final Object eventsMutex_ = new Object();
    
    public SimpleEventLoop() {
        isRunning_ = true;
    }
    
    @Override
    protected void finalize() throws Throwable {
	try {
	    synchronized (eventsMutex_) {
		if (!events_.isEmpty()) {
		    System.err.println("Warning: Pending events in SimpleEventLoop at finalize time");
		}
	    }
	}
	finally {
	    super.finalize();
	}
    }
    
    public void run() {
        doRun(false);
    }
    
    public void runUntilEvents() {
        doRun(true);
    }
    
    private void doRun(boolean breakAfterEvents) {
        while (isRunning_) {
            List<Event> events = new ArrayList<Event>();
            synchronized (eventsMutex_) {
                while (events_.isEmpty()) {
                    try {
                        eventsMutex_.wait();
                    } catch (InterruptedException e) {
                        // Ignore
                    }
                }
                swapCollectionContents(events, events_);
            }
            for (Event event : events) {
                handleEvent(event);
            }
            if (breakAfterEvents) {
                return;
            }
        }
    }

    public void runOnce() {
        List<Event> events = new ArrayList<Event>();
        synchronized (eventsMutex_) {
            swapCollectionContents(events, events_);
        }
        for (Event event : events) {
            handleEvent(event);
        }
    }
    
    public void stop() {
        postEvent(new Event.Callback() {
            
            @Override
            public void run() {
                doStop();
            }
            
        });
    }
    
    private void doStop() {
        isRunning_ = false;
    }

    static <T> void swapCollectionContents(Collection<T> coll1, Collection<T> coll2) {
        Collection<T> temp = new ArrayList<T>(coll1);
        coll1.clear();
        coll1.addAll(coll2);
        coll2.clear();
        coll2.addAll(temp);
    }
    
    @Override
    protected void post(Event event) {
        synchronized (eventsMutex_) {
            events_.add(event);
            eventsMutex_.notifyAll();
        }
    }

}
