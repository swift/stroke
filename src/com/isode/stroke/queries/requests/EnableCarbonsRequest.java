/*
 * Copyright (c) 2016-2017, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.queries.requests;

import com.isode.stroke.elements.CarbonsEnable;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.jid.JID;
import com.isode.stroke.queries.GenericRequest;
import com.isode.stroke.queries.IQRouter;

public class EnableCarbonsRequest extends GenericRequest<CarbonsEnable> {

	public EnableCarbonsRequest(IQRouter router) {
		super(IQ.Type.Set, new JID(), new CarbonsEnable(), router);
	}

	public static EnableCarbonsRequest create(IQRouter router) {
		return new EnableCarbonsRequest(router);
	}
}