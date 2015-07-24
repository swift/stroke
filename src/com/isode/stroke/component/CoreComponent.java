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

import com.isode.stroke.base.Error;
import com.isode.stroke.component.ComponentConnector;
import com.isode.stroke.component.ComponentSession;
import com.isode.stroke.component.ComponentError;
import com.isode.stroke.component.ComponentSessionStanzaChannel;
import com.isode.stroke.elements.Presence;
import com.isode.stroke.elements.Message;
import com.isode.stroke.elements.StreamType;
import com.isode.stroke.jid.JID;
import com.isode.stroke.parser.payloadparsers.FullPayloadParserFactoryCollection;
import com.isode.stroke.serializer.payloadserializers.FullPayloadSerializerCollection;
import com.isode.stroke.entity.Entity;
import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.eventloop.EventLoop;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.network.NetworkFactories;
import com.isode.stroke.network.Connection;
import com.isode.stroke.session.BasicSessionStream;
import com.isode.stroke.session.SessionStream;
import com.isode.stroke.signals.Signal1;
import com.isode.stroke.signals.Signal;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.client.StanzaChannel;
import com.isode.stroke.tls.TLSOptions;

/**
 * The central class for communicating with an XMPP server as a component.
 *
 * This class is responsible for setting up the connection with the XMPP 
 * server and authenticating the component.
 *
 * This class can be used directly in your application, although the Component 
 * subclass provides more functionality and interfaces, and is better suited 
 * for most needs.
 */
public class CoreComponent extends Entity {

	private NetworkFactories networkFactories;
	private JID jid_;
	private String secret_;
	private ComponentSessionStanzaChannel stanzaChannel_;
	private IQRouter iqRouter_;
	private ComponentConnector connector_;
	private Connection connection_;
	private BasicSessionStream sessionStream_;
	private ComponentSession session_;
	private boolean disconnectRequested_;
	private SignalConnection onMessageReceivedConnection;
	private SignalConnection onPresenceReceivedConnection;
	private SignalConnection onAvailableChangedConnection;
	private SignalConnection onConnectFinishedConnection;
	private SignalConnection onDataReadConnection;
	private SignalConnection onDataWrittenConnection;
	private SignalConnection onFinishedConnection;

	public final Signal1<ComponentError> onError = new Signal1<ComponentError>();
	public final Signal onConnected = new Signal();
	public final Signal1<SafeByteArray> onDataRead = new Signal1<SafeByteArray>();
	public final Signal1<SafeByteArray> onDataWritten = new Signal1<SafeByteArray>();

	public final Signal1<Message> onMessageReceived = new Signal1<Message>();
	public final Signal1<Presence> onPresenceReceived = new Signal1<Presence>();

	public CoreComponent(final JID jid, final String secret, NetworkFactories networkFactories) {
		this.networkFactories = networkFactories;
		this.jid_ = jid;
		this.secret_ = secret;
		this.disconnectRequested_ = false;
		stanzaChannel_ = new ComponentSessionStanzaChannel();
		onMessageReceivedConnection = stanzaChannel_.onMessageReceived.connect(onMessageReceived);
		onPresenceReceivedConnection = stanzaChannel_.onPresenceReceived.connect(onPresenceReceived);
		onAvailableChangedConnection = stanzaChannel_.onAvailableChanged.connect(new Slot1<Boolean>() {
			@Override
			public void call(Boolean b1) {
				handleStanzaChannelAvailableChanged(b1);
			}
		});
		iqRouter_ = new IQRouter(stanzaChannel_);
		iqRouter_.setFrom(jid);
	}

	/**
	* This method needs to be called for the object to be eligible for garbage collection.
	*/
	public void delete() {
		if (session_ != null || connection_ != null) {
			System.err.println("Warning: Component not disconnected properly\n");
		}
		onAvailableChangedConnection.disconnect();
		onMessageReceivedConnection.disconnect();
		onAvailableChangedConnection.disconnect();
	}

	public void connect(final String host, int port) {
		assert(connector_ == null);
		connector_ = ComponentConnector.create(host, port, networkFactories.getDomainNameResolver(), networkFactories.getConnectionFactory(), networkFactories.getTimerFactory());
		onConnectFinishedConnection = connector_.onConnectFinished.connect(new Slot1<Connection>() {
			@Override
			public void call(Connection c1) {
				handleConnectorFinished(c1);
			}
		});
		connector_.setTimeoutMilliseconds(60*1000);
		connector_.start();
	}

	public void disconnect() {
		// FIXME: We should be able to do without this boolean. We just have to make sure we can tell the difference between
		// connector finishing without a connection due to an error or because of a disconnect.
		disconnectRequested_ = true;
		if (session_ != null) {
			session_.finish();
		}
		else if (connector_ != null) {
			connector_.stop();
			assert(session_ == null);
		}
		//assert(!session_); /* commenting out until we have time to refactor to be like CoreClient */
		//assert(!sessionStream_);
		//assert(!connector_);
		disconnectRequested_ = false;
	}

	public void sendMessage(Message message) {
		stanzaChannel_.sendMessage(message);
	}

	public void sendPresence(Presence presence) {
		stanzaChannel_.sendPresence(presence);
	}

	public void sendData(final String data) {
		sessionStream_.writeData(data);
	}

	public IQRouter getIQRouter() {
		return iqRouter_;
	}

	public StanzaChannel getStanzaChannel() {
		return stanzaChannel_;
	}

	public boolean isAvailable() {
		return stanzaChannel_.isAvailable();
	}

	/**
	 * Returns the JID of the component
	 */
	public JID getJID() {
		return jid_;
	}

	private void handleConnectorFinished(Connection connection) {
		onConnectFinishedConnection.disconnect();
		connector_ = null;
		if (connection == null) {
			if (!disconnectRequested_) {
				onError.emit(new ComponentError(ComponentError.Type.ConnectionError));
			}
		}
		else {
			assert(connection_ == null);
			connection_ = connection;

			assert(sessionStream_ == null);
			sessionStream_ = new BasicSessionStream(StreamType.ComponentStreamType, connection_, getPayloadParserFactories(), getPayloadSerializers(), null, networkFactories.getTimerFactory(), new TLSOptions());
			onDataReadConnection = sessionStream_.onDataRead.connect(new Slot1<SafeByteArray>() {
				@Override
				public void call(SafeByteArray s1) {
					handleDataRead(s1);
				}
			});
			onDataWrittenConnection = sessionStream_.onDataWritten.connect(new Slot1<SafeByteArray>() {
				@Override
				public void call(SafeByteArray s1) {
					handleDataWritten(s1);
				}
			});

			session_ = ComponentSession.create(jid_, secret_, sessionStream_, networkFactories.getCryptoProvider());
			stanzaChannel_.setSession(session_);
			onFinishedConnection = session_.onFinished.connect(new Slot1<com.isode.stroke.base.Error>() {
				@Override
				public void call(com.isode.stroke.base.Error e1) {
					handleSessionFinished(e1);
				}
			});
			session_.start();
		}
	}

	private void handleStanzaChannelAvailableChanged(boolean available) {
		if (available) {
			onConnected.emit();
		}
	}

	private void handleSessionFinished(Error error) {
		onFinishedConnection.disconnect();
		session_ = null;

		onDataReadConnection.disconnect();
		onDataWrittenConnection.disconnect();
		sessionStream_ = null;

		connection_.disconnect();
		connection_ = null;

		if (error != null) {
			ComponentError componentError = new ComponentError();
			if(error instanceof ComponentSession.Error) {
				ComponentSession.Error actualError = (ComponentSession.Error)error;
				switch(actualError.type) {
					case AuthenticationFailedError:
						componentError = new ComponentError(ComponentError.Type.AuthenticationFailedError);
						break;
					case UnexpectedElementError:
						componentError = new ComponentError(ComponentError.Type.UnexpectedElementError);
						break;
				}
			}
			else if(error instanceof SessionStream.SessionStreamError) {
				SessionStream.SessionStreamError actualError = (SessionStream.SessionStreamError)error;
				switch(actualError.type) {
					case ParseError:
						componentError = new ComponentError(ComponentError.Type.XMLError);
						break;
					case TLSError:
						assert(false);
						componentError = new ComponentError(ComponentError.Type.UnknownError);
						break;
					case InvalidTLSCertificateError:
						assert(false);
						componentError = new ComponentError(ComponentError.Type.UnknownError);
						break;
					case ConnectionReadError:
						componentError = new ComponentError(ComponentError.Type.ConnectionReadError);
						break;
					case ConnectionWriteError:
						componentError = new ComponentError(ComponentError.Type.ConnectionWriteError);
						break;
				}
			}
			onError.emit(componentError);
		}
	}

	private void handleDataRead(SafeByteArray data) {
		onDataRead.emit(data);
	}

	private void handleDataWritten(SafeByteArray data) {
		onDataWritten.emit(data);
	}
}
