/*
 * Copyright (c) 2010-2014 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.component;

import com.isode.stroke.jid.JID;
import com.isode.stroke.elements.Stanza;
import com.isode.stroke.elements.Element;
import com.isode.stroke.elements.ProtocolHeader;
import com.isode.stroke.elements.ComponentHandshake;
import com.isode.stroke.elements.StreamFeatures;
import com.isode.stroke.session.SessionStream;
import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.crypto.JavaCryptoProvider;
import com.isode.stroke.signals.Signal1;
import com.isode.stroke.signals.Signal;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot1;

public class ComponentSession {

	public enum State {
		Initial,
		WaitingForStreamStart,
		Authenticating,
		Initialized,
		Finishing,
		Finished
	};

	public static class Error implements com.isode.stroke.base.Error {
		public enum Type {
			AuthenticationFailedError,
			UnexpectedElementError
		}
		public Type type;
		public Error(Type type) {
			if (type == null) {
				throw new IllegalStateException();
			}
			this.type = type;
		}
	};

	private JID jid = new JID();
	private String secret = "";
	private SessionStream stream;
	private CryptoProvider crypto;
	private com.isode.stroke.base.Error error;
	private State state;
	public final Signal onInitialized = new Signal();
	public final Signal1<com.isode.stroke.base.Error> onFinished = new Signal1<com.isode.stroke.base.Error>();
	public final Signal1<Stanza> onStanzaReceived = new Signal1<Stanza>();
	private SignalConnection onStreamStartReceivedConnection;
	private SignalConnection onElementReceivedConnection;
	private SignalConnection onClosedConnection;
	
	public static ComponentSession create(final JID jid, final String secret, SessionStream stream, CryptoProvider crypto) {
		return new ComponentSession(jid, secret, stream, crypto);
	}

	public State getState() {
		return state;
	}

	public void start() {
		onStreamStartReceivedConnection = stream.onStreamStartReceived.connect(new Slot1<ProtocolHeader>() {
			@Override
			public void call(ProtocolHeader p1) {
				handleStreamStart(p1);
			}
		});
		onElementReceivedConnection = stream.onElementReceived.connect(new Slot1<Element>() {
			@Override
			public void call(Element e1) {
				handleElement(e1);
			}
		});
		onClosedConnection = stream.onClosed.connect(new Slot1<com.isode.stroke.base.Error>() {
			@Override
			public void call(com.isode.stroke.base.Error e1) {
				handleStreamClosed(e1);
			}
		});

		assert(State.Initial.equals(state));
		state = State.WaitingForStreamStart;
		sendStreamHeader();
	}

	public void finish() {
		finishSession((Error.Type)null);
	}

	public void sendStanza(Stanza stanza) {
		stream.writeElement(stanza);
	}

	private ComponentSession(final JID jid, final String secret, SessionStream stream, CryptoProvider crypto) {
		this.jid = jid;
		this.secret = secret;
		this.stream = stream;
		this.crypto = crypto;
		this.state = State.Initial;
	}

	private void finishSession(Error.Type error) {
		Error localError = null;
		if (error != null) {
			localError = new Error(error);
		}
		finishSession(localError);
	}

	private void finishSession(com.isode.stroke.base.Error finishError) {
		state = State.Finishing;
		error = finishError;
		assert(stream.isOpen() == true);
		stream.writeFooter();
		stream.close();
	}

	private void sendStreamHeader() {
		ProtocolHeader header = new ProtocolHeader();
		header.setTo(jid.toString());
		stream.writeHeader(header);
	}

	private void handleElement(Element element) {
		if(element instanceof Stanza) {
			Stanza stanza = (Stanza)element;
			if (State.Initialized.equals(getState())) {
				onStanzaReceived.emit(stanza);
			}
			else {
				finishSession(Error.Type.UnexpectedElementError);
			}
		}
		else if (element instanceof ComponentHandshake) {
			if (!checkState(State.Authenticating)) {
				return;
			}
			stream.setWhitespacePingEnabled(true);
			state = State.Initialized;
			onInitialized.emit();
		}
		else if (State.Authenticating.equals(getState())) {
			if (element instanceof StreamFeatures) {
				// M-Link sends stream features, so swallow that.
			}
			else {
				// FIXME: We should actually check the element received
				finishSession(Error.Type.AuthenticationFailedError);
			}
		}
		else {
			finishSession(Error.Type.UnexpectedElementError);
		}
	}

	private void handleStreamStart(final ProtocolHeader header) {
		checkState(State.WaitingForStreamStart);
		state = State.Authenticating;
		stream.writeElement(new ComponentHandshake(ComponentHandshakeGenerator.getHandshake(header.getID(), secret, crypto)));
	}

	private void handleStreamClosed(com.isode.stroke.base.Error streamError) {
		State oldState = state;
		state = State.Finished;
		stream.setWhitespacePingEnabled(false);
		onStreamStartReceivedConnection.disconnect();
		onElementReceivedConnection.disconnect();
		onClosedConnection.disconnect();
		if (State.Finishing.equals(oldState)) {
			onFinished.emit(error);
		}
		else {
			onFinished.emit(streamError);
		}
	}

	private boolean checkState(State state) {
		if (!(this.state.equals(state))) {
			finishSession(Error.Type.UnexpectedElementError);
			return false;
		}
		return true;
	}
}



