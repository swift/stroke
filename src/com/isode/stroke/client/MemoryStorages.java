/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.client;

import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.disco.CapsMemoryStorage;
import com.isode.stroke.disco.CapsStorage;
import com.isode.stroke.roster.RosterMemoryStorage;
import com.isode.stroke.roster.RosterStorage;
import com.isode.stroke.vcards.VCardMemoryStorage;
import com.isode.stroke.vcards.VCardStorage;

public class MemoryStorages implements Storages {
	private VCardStorage vcardStorage;
//	private AvatarStorage avatarStorage;
	private CapsStorage capsStorage;
	private RosterStorage rosterStorage;
//	private HistoryStorage historyStorage;
	
	public MemoryStorages(CryptoProvider crypto) {
		vcardStorage = new VCardMemoryStorage(crypto);
		capsStorage = new CapsMemoryStorage();
//		avatarStorage = new AvatarMemoryStorage();
		rosterStorage = new RosterMemoryStorage();
//	#ifdef SWIFT_EXPERIMENTAL_HISTORY
//		historyStorage = new SQLiteHistoryStorage(":memory:");
//	#else
//		historyStorage = NULL;
		
	}

	@Override
	public VCardStorage getVCardStorage() {
		return vcardStorage;
	}

	@Override
	public RosterStorage getRosterStorage() {
		return rosterStorage;
	}

    @Override
    public CapsStorage getCapsStorage() {
        return capsStorage;
    }

}
