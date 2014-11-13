/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.vcards;

import com.isode.stroke.elements.IQ;
import com.isode.stroke.elements.VCard;
import com.isode.stroke.jid.JID;
import com.isode.stroke.queries.GenericRequest;
import com.isode.stroke.queries.IQRouter;

public class GetVCardRequest extends GenericRequest<VCard> {
	public static GetVCardRequest create(final JID jid, IQRouter router) {
		return new GetVCardRequest(jid, router);
	}

	private GetVCardRequest(final JID jid, IQRouter router) {
		super(IQ.Type.Get, jid, new VCard(), router);
	}

}
