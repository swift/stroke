/*
 * Copyright (c) 2011-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.roster;

import com.isode.stroke.elements.RosterPayload;

public class RosterMemoryStorage implements RosterStorage {
	private RosterPayload roster = new RosterPayload();

	@Override
	public RosterPayload getRoster() {
		return roster;
	}

	@Override
	public void setRoster(RosterPayload r) {
		roster = r;
	}

}
