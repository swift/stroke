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

import com.isode.stroke.queries.Request;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.elements.InBandRegistrationPayload;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.signals.Signal2;
import com.isode.stroke.jid.JID;

public class SetInBandRegistrationRequest extends Request {

	public final Signal2<Payload, ErrorPayload> onResponse = new Signal2<Payload, ErrorPayload>();

	public SetInBandRegistrationRequest(final JID to, InBandRegistrationPayload payload, IQRouter router) {
		super(IQ.Type.Set, to, (InBandRegistrationPayload)payload, router);
	}

	public static SetInBandRegistrationRequest create(final JID to, InBandRegistrationPayload payload, IQRouter router) {
		return new SetInBandRegistrationRequest(to, payload, router);
	}

	protected void handleResponse(Payload payload, ErrorPayload error) {
		onResponse.emit(payload, error);
	}
}