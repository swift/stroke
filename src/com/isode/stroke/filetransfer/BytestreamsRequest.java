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
import com.isode.stroke.elements.Bytestreams;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.jid.JID;

public class BytestreamsRequest extends GenericRequest<Bytestreams> {

	public static BytestreamsRequest create(final JID jid, Bytestreams payload, IQRouter router) {
		return new BytestreamsRequest(jid, payload, router);
	}

	public static BytestreamsRequest create(final JID from, final JID to, Bytestreams payload, IQRouter router) {
		return new BytestreamsRequest(from, to, payload, router);
	}

	private BytestreamsRequest(final JID jid, Bytestreams payload, IQRouter router) {
		super(IQ.Type.Set, jid, payload, router);
	}

	private BytestreamsRequest(final JID from, final JID to, Bytestreams payload, IQRouter router) {
		super(IQ.Type.Set, from, to, payload, router);
	}
}
