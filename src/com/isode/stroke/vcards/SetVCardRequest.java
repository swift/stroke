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

public class SetVCardRequest extends GenericRequest<VCard> {

	public static SetVCardRequest create(VCard vcard, IQRouter router) {
		return new SetVCardRequest(vcard, router);
	}

	private	SetVCardRequest(VCard vcard, IQRouter router) {
		super(IQ.Type.Set, new JID(), vcard, router);
	}

}
