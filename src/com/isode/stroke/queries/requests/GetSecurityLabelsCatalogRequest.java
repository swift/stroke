/*
 * Copyright (c) 2010-2015, Isode Limited, London, England.
 * All rights reserved.
 */
package com.isode.stroke.queries.requests;

import com.isode.stroke.elements.IQ;
import com.isode.stroke.elements.SecurityLabelsCatalog;
import com.isode.stroke.jid.JID;
import com.isode.stroke.queries.GenericRequest;
import com.isode.stroke.queries.IQRouter;

public class GetSecurityLabelsCatalogRequest extends GenericRequest<SecurityLabelsCatalog>{

	public static GetSecurityLabelsCatalogRequest create(JID recipient, IQRouter router) {
		return new GetSecurityLabelsCatalogRequest(recipient, router);
	}
	
	private GetSecurityLabelsCatalogRequest(JID recipient, IQRouter router) {
		super(IQ.Type.Get, new JID(), new SecurityLabelsCatalog(recipient), router);
	}
}
