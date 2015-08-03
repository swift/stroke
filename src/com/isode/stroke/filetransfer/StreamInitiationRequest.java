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

package com.isode.stroke.filetransfer;

import com.isode.stroke.queries.GenericRequest;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.elements.StreamInitiation;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.jid.JID;

public class StreamInitiationRequest extends GenericRequest<StreamInitiation> {

	public static StreamInitiationRequest create(final JID jid, StreamInitiation payload, IQRouter router) {
		return new StreamInitiationRequest(jid, payload, router);
	}

	public static StreamInitiationRequest create(final JID from, final JID to, StreamInitiation payload, IQRouter router) {
		return new StreamInitiationRequest(from, to, payload, router);
	}

	private StreamInitiationRequest(final JID jid, StreamInitiation payload, IQRouter router) {
		super(IQ.Type.Set, jid, payload, router);
	}

	private StreamInitiationRequest(final JID from, final JID to, StreamInitiation payload, IQRouter router) {
		super(IQ.Type.Set, from, to, payload, router);
	}
}