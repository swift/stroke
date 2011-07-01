/*
 * Copyright (c) 2010 Remko Tronçon
 * Licensed under the GNU General Public License v3.
 * See Documentation/Licenses/GPLv3.txt for more information.
 */
/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.eventloop;

import java.util.ArrayList;
import java.util.Vector;

public abstract class EventLoop {

    public EventLoop() {
    }

    public void postEvent(Event.Callback callback) {
        postEvent(callback, null);
    }

    public void postEvent(Event.Callback callback, EventOwner owner) {
        Event event;
        synchronized (eventsMutex_) {
            event = new Event(owner, callback, nextEventID_);
            nextEventID_++;
            events_.add(event);
        }
        post(event);
    }

    public void removeEventsFromOwner(EventOwner owner) {
        synchronized (eventsMutex_) {
            ArrayList<Event> matches = new ArrayList<Event>();
            for (Event event : events_) {
                if (event.owner == owner) {
                    matches.add(event);
                }
            }
            events_.removeAll(matches);
        }
    }

    /**
     * Reimplement this to call handleEvent(event) from the thread in which
     * the event loop is residing.
     */
    protected abstract void post(Event event);

    protected void handleEvent(Event event) {
        if (handlingEvents_) {
            // We're being called recursively. Push in the list of events to
            // handle in the parent handleEvent()
            eventsToHandle_.add(event);
            return;
        }

        boolean doCallback = false;
        synchronized (eventsMutex_) {
            doCallback = events_.contains(event);
            if (doCallback) {
                events_.remove(event);
            }
        }
        if (doCallback) {
            handlingEvents_ = true;
            event.callback.run();
            // Process events that were passed to handleEvent during the callback
            // (i.e. through recursive calls of handleEvent)
            while (!eventsToHandle_.isEmpty()) {
                Event nextEvent = eventsToHandle_.firstElement();
                eventsToHandle_.remove(0);
                nextEvent.callback.run();
            }
            handlingEvents_ = false;
        }
    }
    //	struct HasOwner {
    //		HasOwner(boost::shared_ptr<EventOwner> owner) : owner(owner) {}
    //		bool operator()(const Event& event) { return event.owner == owner; }
    //		boost::shared_ptr<EventOwner> owner;
    //	};
    private final Object eventsMutex_ = new Object();
    private int nextEventID_ = 0;
    private Vector<Event> events_ = new Vector<Event>();
    boolean handlingEvents_ = false;
    private Vector<Event> eventsToHandle_ = new Vector<Event>();
}
