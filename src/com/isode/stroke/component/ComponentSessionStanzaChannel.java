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

package com.isode.stroke.component;

import com.isode.stroke.base.IDGenerator;
import com.isode.stroke.component.ComponentSession;
import com.isode.stroke.client.StanzaChannel;
import com.isode.stroke.elements.Message;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.elements.Presence;
import com.isode.stroke.elements.Stanza;
import com.isode.stroke.tls.Certificate;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot;
import com.isode.stroke.signals.Slot1;
import java.util.Vector;

public class ComponentSessionStanzaChannel extends StanzaChannel {

	private IDGenerator idGenerator;
	private ComponentSession session;
	private SignalConnection onInitializedConnection;
	private SignalConnection onFinishedConnection;
	private SignalConnection onStanzaReceivedConnection;

	public void setSession(ComponentSession session) {
		assert(this.session == null);
		this.session = session;
		onInitializedConnection = session.onInitialized.connect(new Slot() {
			@Override
			public void call() {
				handleSessionInitialized();
			}
		});
		onFinishedConnection = session.onFinished.connect(new Slot1<com.isode.stroke.base.Error>() {
			@Override
			public void call(com.isode.stroke.base.Error e1) {
				handleSessionFinished(e1);
			}
		});
		onStanzaReceivedConnection = session.onStanzaReceived.connect(new Slot1<Stanza>() {
			@Override
			public void call(Stanza s1) {
				handleStanza(s1);
			}
		});
	}

	public void sendIQ(IQ iq) {
		send(iq);
	}

	public void sendMessage(Message message) {
		send(message);
	}

	public void sendPresence(Presence presence) {
		send(presence);
	}

	public boolean getStreamManagementEnabled() {
		return false;
	}

	public Vector<Certificate> getPeerCertificateChain() {
		// TODO: actually implement this method
		return (Vector<Certificate>)null;
	}

	public boolean isAvailable() {
		return (session != null) && (ComponentSession.State.Initialized.equals(session.getState()));
	}

	public String getNewIQID() {
		return idGenerator.generateID();
	}

	private void send(Stanza stanza) {
		if (!isAvailable()) {
			System.err.println("Warning: Component: Trying to send a stanza while disconnected.\n");
			return;
		}
		session.sendStanza(stanza);
	}

	private void handleSessionFinished(com.isode.stroke.base.Error error) {
		onFinishedConnection.disconnect();
		onStanzaReceivedConnection.disconnect();
		onInitializedConnection.disconnect();
		session = null;

		onAvailableChanged.emit(false);
	}

	private void handleStanza(Stanza stanza) {
		if(stanza instanceof Message) {
			Message message = (Message)(stanza);
			onMessageReceived.emit(message);
			return;
		}

		if(stanza instanceof Presence) {
			Presence presence = (Presence)(stanza);
			onPresenceReceived.emit(presence);
			return;
		}

		if(stanza instanceof IQ) {
			IQ iq = (IQ)(stanza);
			onIQReceived.emit(iq);
			return;
		}
	}

	private void handleSessionInitialized() {
		onAvailableChanged.emit(true);
	}
}