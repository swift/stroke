/*
 * Copyright (c) 2011-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.roster;

import com.isode.stroke.elements.RosterPayload;

public interface RosterStorage {

	RosterPayload getRoster();
	void setRoster(RosterPayload roster);
}
