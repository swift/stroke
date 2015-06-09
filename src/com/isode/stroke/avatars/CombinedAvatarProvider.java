/*
 * Copyright (c) 2010 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.avatars;

import com.isode.stroke.avatars.AvatarProvider;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.jid.JID;
import java.util.logging.Logger;
import java.util.*;

public class CombinedAvatarProvider implements AvatarProvider {

	private Vector<AvatarProvider> providers = new Vector<AvatarProvider>();
	private Map<JID, String> avatars = new HashMap<JID, String>();
	private SignalConnection onAvatarChangedConnection_;
	private Logger logger_ = Logger.getLogger(this.getClass().getName());

	public String getAvatarHash(JID jid) {
		return getCombinedAvatarAndCache(jid);
	}

	public void addProvider(AvatarProvider provider) {
		onAvatarChangedConnection_ = provider.onAvatarChanged.connect(new Slot1<JID>() {

			public void call(JID p1) {
				handleAvatarChanged(p1);
			}
		});
		providers.add(provider);
	}

	public void removeProvider(AvatarProvider provider) {
		while(providers.contains(provider)) {
			providers.remove(provider);
			onAvatarChangedConnection_.disconnect();
		}
	}

	private void handleAvatarChanged(JID jid) {
		String oldHash = new String();
		if(avatars.containsKey(jid)) {
			oldHash = avatars.get(jid);
		}
		String newHash = getCombinedAvatarAndCache(jid);
		if (newHash != null && !newHash.equals(oldHash)) {
			logger_.fine("Avatar changed: " + jid + ": " + oldHash + " -> " + ((newHash != null) ? newHash : "NULL") + "\n");
			onAvatarChanged.emit(jid);
		}
	}

	private String getCombinedAvatarAndCache(JID jid) {
		logger_.fine("JID: " + jid + "\n");
		String hash = null;
		for (int i = 0; i < providers.size() && (hash==null); ++i) {
			hash = providers.get(i).getAvatarHash(jid);
			logger_.fine("Provider " + providers.get(i) + ": " + ((hash != null) ? hash : "NULL") + "\n");
		}
		if (hash != null) {
			avatars.put(jid, hash);
		} else {
			avatars.put(jid, "");
		}
		return hash;
	}
}