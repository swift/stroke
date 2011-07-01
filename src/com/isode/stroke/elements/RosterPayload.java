/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tron?on.
 * All rights reserved.
 */
package com.isode.stroke.elements;

import com.isode.stroke.jid.JID;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Roster.
 */
public class RosterPayload extends Payload {

    public RosterPayload() {
    }

    public RosterItemPayload getItem(JID jid) {
        for (RosterItemPayload item : items_) {
            if (item.getJID().equals(jid)) {
                return item;
            }
        }
        return null;
    }

    public void addItem(RosterItemPayload item) {
        items_.add(item);
    }

    public Collection<RosterItemPayload> getItems() {
        return items_;
    }

    private final ArrayList<RosterItemPayload> items_ = new ArrayList<RosterItemPayload>();
}
