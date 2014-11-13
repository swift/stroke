/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.queries;

import com.isode.stroke.elements.Payload;
import com.isode.stroke.jid.JID;

public abstract class SetResponder<T extends Payload> extends Responder<T> {

	public SetResponder(T payloadType, IQRouter router) {
		super(payloadType, router);
	}

	@Override
	protected boolean handleGetRequest(JID from, JID to, String id, T payload) {
		return false;
	}
}
