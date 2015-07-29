/*
 * Copyright (c) 2011-2013 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.jingle;

import java.util.logging.Logger;
import java.util.Vector;
import java.util.Map;
import java.util.HashMap;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.elements.JingleContentPayload;
import com.isode.stroke.jid.JID;

public class JingleSessionManager {

	private IQRouter router;
	private JingleResponder responder;
	private Vector<IncomingJingleSessionHandler> incomingSessionHandlers = new Vector<IncomingJingleSessionHandler>();
	private Logger logger_ = Logger.getLogger(this.getClass().getName());

	private class JIDSession {
		public JIDSession(final JID initiator, final String session) {
			this.initiator = initiator;
			this.session = session;
		}
		public int compareTo(JIDSession other) {
			if(other == null) {
				return -1;
			}
			if (initiator.equals(other.initiator)) {
				return session.compareTo(other.session);
			}
			else {
				return initiator.compareTo(other.initiator);
			}
		}
		public JID initiator;
		public String session;
	};

	private Map<JIDSession, JingleSessionImpl> sessions = new HashMap<JIDSession, JingleSessionImpl>();

	public JingleSessionManager(IQRouter router) {
		this.router = router;
		responder = new JingleResponder(this, router);
		responder.start();
	}

	public JingleSessionImpl getSession(final JID jid, final String id) {
		return sessions.get(new JIDSession(jid, id));
	}

	public void addIncomingSessionHandler(IncomingJingleSessionHandler handler) {
		incomingSessionHandlers.add(handler);
	}

	public void removeIncomingSessionHandler(IncomingJingleSessionHandler handler) {
		incomingSessionHandlers.remove(handler);
	}

	public void registerOutgoingSession(final JID initiator, JingleSessionImpl session) {
		sessions.put(new JIDSession(initiator, session.getID()), session);
		logger_.fine("Added session " + session.getID() + " for initiator " + initiator.toString() + "\n");
	}

	protected void handleIncomingSession(final JID initiator, final JID recipient, JingleSessionImpl session, final Vector<JingleContentPayload> contents) {
		sessions.put(new JIDSession(initiator, session.getID()), session);
		for (IncomingJingleSessionHandler handler : incomingSessionHandlers) {
			if (handler.handleIncomingJingleSession(session, contents, recipient)) {
				return;
			}
		}
		// TODO: Finish session
	}
}