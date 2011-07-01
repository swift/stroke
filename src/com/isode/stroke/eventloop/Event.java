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
}
