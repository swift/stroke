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

	private static class JIDSession {
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
		@Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((initiator == null) ? 0 : initiator.hashCode());
            result = prime * result
                    + ((session == null) ? 0 : session.hashCode());
            return result;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            JIDSession other = (JIDSession) obj;
            if (initiator == null) {
                if (other.initiator != null)
                    return false;
            } else if (!initiator.equals(other.initiator))
                return false;
            if (session == null) {
                if (other.session != null)
                    return false;
            } else if (!session.equals(other.session))
                return false;
            return true;
        }
        public final JID initiator;
		public final String session;
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