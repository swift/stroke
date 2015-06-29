/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.client;

import com.isode.stroke.avatars.AvatarStorage;
import com.isode.stroke.disco.CapsStorage;
import com.isode.stroke.roster.RosterStorage;
import com.isode.stroke.vcards.VCardStorage;

public interface Storages {
	VCardStorage getVCardStorage();
	AvatarStorage getAvatarStorage();
	RosterStorage getRosterStorage();
//	HistoryStorage getHistoryStorage();
    CapsStorage getCapsStorage();
}
