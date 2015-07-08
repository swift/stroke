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
import com.isode.stroke.elements.InBandRegistrationPayload;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.jid.JID;

public class GetInBandRegistrationFormRequest extends GenericRequest<InBandRegistrationPayload> {

	public GetInBandRegistrationFormRequest(final JID to, IQRouter router) {
		super(IQ.Type.Get, to, new InBandRegistrationPayload(), router);
	}

	public static GetInBandRegistrationFormRequest create(final JID to, IQRouter router) {
		return new GetInBandRegistrationFormRequest(to, router);
	}
}