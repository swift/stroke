/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.roster;

import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.elements.RosterPayload;
import com.isode.stroke.jid.JID;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.queries.SetResponder;
import com.isode.stroke.signals.Signal1;

public class RosterPushResponder extends SetResponder<RosterPayload> {

	public final Signal1<RosterPayload> onRosterReceived = new Signal1<RosterPayload>();

	public RosterPushResponder(IQRouter router) {
		super(new RosterPayload(), router);
	}

	@Override
	protected boolean handleSetRequest(JID from, JID to, String id, RosterPayload payload) {
			if (getIQRouter().isAccountJID(from)) {
				onRosterReceived.emit(payload);
				sendResponse(from, id, new RosterPayload());
			} else {
				sendError(from, id, ErrorPayload.Condition.NotAuthorized, ErrorPayload.Type.Cancel);
			}
			return true;
	}


}
