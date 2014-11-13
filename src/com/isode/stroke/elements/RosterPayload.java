/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tron?on.
 * All rights reserved.
 */
package com.isode.stroke.elements;

import com.isode.stroke.jid.JID;

import java.util.ArrayList;
import java.util.List;

/**
 * Roster.
 */
public class RosterPayload extends Payload {
    private final ArrayList<RosterItemPayload> items_ = new ArrayList<RosterItemPayload>();
	private String version_;

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

    public List<RosterItemPayload> getItems() {
        return items_;
    }

	public String getVersion() {
		return version_;
	}

	public void setVersion(String version) {
		this.version_ = version;
	}

}
