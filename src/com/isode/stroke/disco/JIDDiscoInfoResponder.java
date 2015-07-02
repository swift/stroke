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

import com.isode.stroke.jid.JID;
import com.isode.stroke.elements.DiscoInfo;
import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.queries.GetResponder;
import com.isode.stroke.queries.IQRouter;
import java.util.Map;
import java.util.HashMap;

public class JIDDiscoInfoResponder extends GetResponder<DiscoInfo> {

	private class JIDDiscoInfo {
		public DiscoInfo discoInfo;
		public Map<String, DiscoInfo> nodeDiscoInfo = new HashMap<String, DiscoInfo>();
	}

	private Map<JID, JIDDiscoInfo> info = new HashMap<JID, JIDDiscoInfo>();

	public JIDDiscoInfoResponder(IQRouter router) {
		super(new DiscoInfo(), router);
	}

	public void clearDiscoInfo(JID jid) {
		info.remove(jid);
	}

	public void setDiscoInfo(JID jid, DiscoInfo discoInfo) {
		JIDDiscoInfo jdisco = new JIDDiscoInfo();
		jdisco.discoInfo = discoInfo;
		info.put(jid, jdisco);
	}

	public void setDiscoInfo(JID jid, String node, DiscoInfo discoInfo) {
		DiscoInfo newInfo = discoInfo;
		newInfo.setNode(node);
		JIDDiscoInfo jdisco = new JIDDiscoInfo();
		jdisco.nodeDiscoInfo.put(node, newInfo);
		info.put(jid, jdisco);
	}

	protected boolean handleGetRequest(JID from, JID to, String id, DiscoInfo discoInfo) {
		if(info.containsKey(to)) {
			if (discoInfo.getNode().isEmpty()) {
				sendResponse(from, to, id, info.get(to).discoInfo);
			}
			else {
				if(info.get(to).nodeDiscoInfo.containsKey(discoInfo.getNode())) {
					sendResponse(from, to, id, info.get(to).nodeDiscoInfo.get(discoInfo.getNode()));
				}
				else {
					sendError(from, to, id, ErrorPayload.Condition.ItemNotFound, ErrorPayload.Type.Cancel);
				}
			}
		}
		else {
			sendError(from, to, id, ErrorPayload.Condition.ItemNotFound, ErrorPayload.Type.Cancel);
		}
		return true;
	}
}