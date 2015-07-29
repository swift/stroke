/*
 * Copyright (c) 2011-2015 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.jingle;

import com.isode.stroke.queries.SetResponder;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.elements.JinglePayload;
import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.jid.JID;
import java.util.logging.Logger;

public class JingleResponder extends SetResponder<JinglePayload> {

	private JingleSessionManager sessionManager;
	private IQRouter router;
	private Logger logger_ = Logger.getLogger(this.getClass().getName());

	public JingleResponder(JingleSessionManager sessionManager, IQRouter router) {
		super(new JinglePayload(), router);
		this.sessionManager = sessionManager;
		this.router = router;
	}

	public boolean handleSetRequest(final JID from, final JID to, final String id, JinglePayload payload) {
		if (JinglePayload.Action.SessionInitiate.equals(payload.getAction())) {
			if (sessionManager.getSession(from, payload.getSessionID()) != null) {
				// TODO: Add tie-break error
				sendError(from, id, ErrorPayload.Condition.Conflict, ErrorPayload.Type.Cancel);
			}
			else {
				sendResponse(from, id, null);
				if (!payload.getInitiator().isBare()) {
					JingleSessionImpl session = new JingleSessionImpl(payload.getInitiator(), from, payload.getSessionID(), router);
					sessionManager.handleIncomingSession(from, to, session, payload.getContents());
				} else {
					logger_.fine("Unable to create Jingle session due to initiator not being a full JID.\n");
				}
			}
		}
		else {
			JingleSessionImpl session = null;
			if (payload.getInitiator().isValid()) {
				logger_.fine("Lookup session by initiator.\n");
				session = sessionManager.getSession(payload.getInitiator(), payload.getSessionID());
			} else {
				logger_.fine("Lookup session by from attribute.\n");
				session = sessionManager.getSession(from, payload.getSessionID());
			}
			if (session != null) {
				session.handleIncomingAction(payload);
				sendResponse(from, id, null);
			}
			else {
				logger_.warning("Didn't find jingle session!");
				// TODO: Add jingle-specific error
				sendError(from, id, ErrorPayload.Condition.ItemNotFound, ErrorPayload.Type.Cancel);
			}
		}
		return true;
	}
}