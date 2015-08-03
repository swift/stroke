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
import com.isode.stroke.elements.IBB;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.jid.JID;

public class IBBRequest extends GenericRequest<IBB> {

	public static IBBRequest create(final JID from, final JID to, IBB payload, IQRouter router) {
		return new IBBRequest(from, to, payload, router);
	}

	private IBBRequest(final JID from, final JID to, IBB payload, IQRouter router) {
		super(IQ.Type.Set, from, to, payload, router);
	}
}