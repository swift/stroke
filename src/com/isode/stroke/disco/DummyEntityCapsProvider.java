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

package com.isode.stroke.disco;

import com.isode.stroke.disco.EntityCapsProvider;
import com.isode.stroke.jid.JID;
import com.isode.stroke.elements.DiscoInfo;
import java.util.Map;
import java.util.HashMap;

public class DummyEntityCapsProvider extends EntityCapsProvider {

	public Map<JID, DiscoInfo> caps = new HashMap<JID, DiscoInfo>();

	public DummyEntityCapsProvider() {

	}

	public DiscoInfo getCaps(JID jid) {
		if(caps.containsKey(jid)) {
			return caps.get(jid);
		}
		return null;
	}
}