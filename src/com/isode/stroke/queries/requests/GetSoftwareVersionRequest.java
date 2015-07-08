/*
 * Copyright (c) 2010-2015 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.queries.requests;

import com.isode.stroke.queries.GenericRequest;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.elements.SoftwareVersion;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.jid.JID;

public class GetSoftwareVersionRequest extends GenericRequest<SoftwareVersion> {

	public GetSoftwareVersionRequest(final JID recipient, IQRouter router) {
		super(IQ.Type.Get, recipient, new SoftwareVersion(), router);
	}

	public static GetSoftwareVersionRequest create(final JID recipient, IQRouter router) {
		return new GetSoftwareVersionRequest(recipient, router);
	}
}