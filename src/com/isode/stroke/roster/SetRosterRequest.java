/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.roster;

import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.elements.RosterPayload;
import com.isode.stroke.jid.JID;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.queries.Request;
import com.isode.stroke.signals.Signal1;

public class SetRosterRequest extends Request {

	static SetRosterRequest create(RosterPayload payload, IQRouter router) {
		return new SetRosterRequest(new JID(), payload, router);
	}

	static SetRosterRequest create(RosterPayload payload, final JID to, IQRouter router) {
		return new SetRosterRequest(to, payload, router);
	}

	private SetRosterRequest(final JID to, RosterPayload payload, IQRouter router) {
		super(IQ.Type.Set, to, payload, router);
	}

	public void handleResponse(Payload payload, ErrorPayload error) {
		onResponse.emit(error);
	}

	final Signal1<ErrorPayload> onResponse = new Signal1<ErrorPayload>();

}
