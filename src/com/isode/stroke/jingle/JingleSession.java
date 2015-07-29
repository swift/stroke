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

import com.isode.stroke.elements.JinglePayload;
import com.isode.stroke.elements.JingleDescription;
import com.isode.stroke.elements.JingleTransportPayload;
import com.isode.stroke.elements.Payload;
import com.isode.stroke.jid.JID;
import com.isode.stroke.base.Listenable;
import java.util.Vector;

public abstract class JingleSession extends Listenable<JingleSessionListener> {

	private JID initiator = new JID();
	private String id = "";
	private Vector<JingleSessionListener> listeners = new Vector<JingleSessionListener>();

	public JingleSession(final JID initiator, final String id) {
		this.initiator = initiator;
		this.id = id;
		// initiator must always be a full JID; session lookup based on it wouldn't work otherwise
		// this is checked on an upper level so that the assert never fails
		assert(!initiator.isBare());
	}

	public JID getInitiator() {
		return initiator;
	}

	public String getID() {
		return id;
	}

	public abstract void sendInitiate(final JingleContentID id, JingleDescription description, JingleTransportPayload transport);
	public abstract void sendTerminate(JinglePayload.Reason.Type reason);
	public abstract void sendInfo(Payload payload);
	public abstract void sendAccept(final JingleContentID id, JingleDescription description);
	public abstract void sendAccept(final JingleContentID id, JingleDescription description, JingleTransportPayload transport);
	public abstract String sendTransportInfo(final JingleContentID id, JingleTransportPayload transport);
	public abstract void sendTransportAccept(final JingleContentID id, JingleTransportPayload transport);
	public abstract void sendTransportReject(final JingleContentID id, JingleTransportPayload transport);
	public abstract void sendTransportReplace(final JingleContentID id, JingleTransportPayload transport);
}