/*
 * Copyright (c) 2010 Remko Tronçon
 * Licensed under the GNU General Public License v3.
 * See Documentation/Licenses/GPLv3.txt for more information.
 */
/*
 * Copyright (c) 2010-2012, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.eventloop;

/**
 * An Event object represents an external event that requires processing. 
 * A concrete {@link EventLoop} implementation must ensure that Events are 
 * processed in the application's main event loop. 
 * 
 * <p>An application (and any concrete class of EventLoop) should treat Events
 * as opaque objects to be passed to {@link EventLoop#handleEvent(Event)}.
 *  
 * <p>Event processing is designed to happen in the main event loop, so for
 * example a GUI application might handle Events in the following manner:
 * <pre>
 *  EventLoop eventLoop = new EventLoop() {
 *     protected void post(final Event event) {
 *       // Invoke the callback in the AWT display thread     
 *       SwingUtilities.invokeLater(new Runnable() {
 *           public void run() {
 *              handleEvent(event);
 *          }
 *       });
 *     }
 *  };
 *  .
 *  .
 *  .
 *  client.onConnected.connect(new Slot() {
 *     public void call() {
 *        // This will always be called inside the event dispatching thread
 *        updateSomeTextFieldInTheGUI("Connected");
 *     }
 *  });
 * </pre>
 * 
 */
public class Event {

    public interface Callback {
        void run();
    }

    Event(EventOwner owner, Callback callback, int id) {
        this.owner = owner;
        this.callback = callback;
        this.id = id;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Event) {
            return id == ((Event) other).id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + this.id;
        return hash;
    }
    final int id;
    final EventOwner owner;
    public final Callback callback;
    
    @Override
    public String toString() {
        return "Event with id=" + id + 
        " callback=" + callback +
        (owner == null ? " (no owner information)" : " owner=" + owner);
        
    }
}
