/*
 * Copyright (c) 2010-2012, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.client;

import com.isode.stroke.base.NotNull;
import com.isode.stroke.elements.Message;
import com.isode.stroke.elements.Presence;
import com.isode.stroke.elements.Stanza;
import com.isode.stroke.elements.StreamType;
import com.isode.stroke.eventloop.EventLoop;
import com.isode.stroke.jid.JID;
import com.isode.stroke.network.Connection;
import com.isode.stroke.network.ConnectionFactory;
import com.isode.stroke.network.Connector;
import com.isode.stroke.network.NetworkFactories;
import com.isode.stroke.network.PlatformDomainNameResolver;
import com.isode.stroke.parser.payloadparsers.FullPayloadParserFactoryCollection;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.serializer.payloadserializers.FullPayloadSerializerCollection;
import com.isode.stroke.session.BasicSessionStream;
import com.isode.stroke.session.SessionStream;
import com.isode.stroke.signals.Signal;
import com.isode.stroke.signals.Signal1;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.tls.Certificate;
import com.isode.stroke.tls.CertificateTrustChecker;
import com.isode.stroke.tls.CertificateVerificationError;
import com.isode.stroke.tls.CertificateWithKey;
import com.isode.stroke.tls.PlatformTLSFactories;

/**
 * The central class for communicating with an XMPP server.
 *
 * This class is responsible for setting up the connection with the XMPP server, authenticating, and
 * initializing the session.
 *
 * This class can be used directly in your application, although the Client subclass provides more
 * functionality and interfaces, and is better suited for most needs.
 */
public class CoreClient {
    private SignalConnection sessionStreamDataReadConnection_;
    private SignalConnection sessionStreamDataWrittenConnection_;
    private SignalConnection sessionFinishedConnection_;
    private SignalConnection sessionNeedCredentialsConnection_;
    private SignalConnection connectorConnectFinishedConnection_;
    private final EventLoop eventLoop_;

    /**
     * Constructor.
     * 
     * @param eventLoop Event loop used by the class, must not be null. The
     *            CoreClient creates threads to do certain tasks. However, it
     *            posts events that it expects to be done in the application's
     *            main thread to this eventLoop. The application should
     *            use an appropriate EventLoop implementation for the application type. This
     *            EventLoop is just a way for the CoreClient to pass these
     *            events back to the main thread, and should not be used by the
     *            application for its own purposes.
     * @param jid User JID used to connect to the server, must not be null
     * @param password User password to use, must not be null
     * @param networkFactories An implementation of network interaction, must
     *            not be null.
     */
    public CoreClient(EventLoop eventLoop, JID jid, String password, NetworkFactories networkFactories) {
        jid_ = jid;
        password_ = password;
        disconnectRequested_ = false;
        eventLoop_ = eventLoop;
        this.networkFactories = networkFactories;
        this.certificateTrustChecker = null;
        resolver_ = new PlatformDomainNameResolver(eventLoop);
        stanzaChannel_ = new ClientSessionStanzaChannel();
        stanzaChannel_.onMessageReceived.connect(new Slot1<Message>() {

            public void call(Message p1) {
                onMessageReceived.emit(p1);
            }
        });
        stanzaChannel_.onPresenceReceived.connect(new Slot1<Presence>() {

            public void call(Presence p1) {
                onPresenceReceived.emit(p1);
            }
        });
        stanzaChannel_.onStanzaAcked.connect(new Slot1<Stanza>() {

            public void call(Stanza p1) {
                onStanzaAcked.emit(p1);
            }
        });
        stanzaChannel_.onAvailableChanged.connect(new Slot1<Boolean>() {

            public void call(Boolean p1) {
                handleStanzaChannelAvailableChanged(p1);
            }
        });

        iqRouter_ = new IQRouter(stanzaChannel_);
        tlsFactories = new PlatformTLSFactories();
    }

    /*CoreClient::~CoreClient() {
    if (session_ || connection_) {
    std::cerr << "Warning: Client not disconnected properly" << std::endl;
    }
    delete tlsLayerFactory_;
    delete timerFactory_;
    delete connectionFactory_;
    delete iqRouter_;

    stanzaChannel_->onAvailableChanged.disconnect(boost::bind(&CoreClient::handleStanzaChannelAvailableChanged, this, _1));
    stanzaChannel_->onMessageReceived.disconnect(boost::ref(onMessageReceived));
    stanzaChannel_->onPresenceReceived.disconnect(boost::ref(onPresenceReceived));
    stanzaChannel_->onStanzaAcked.disconnect(boost::ref(onStanzaAcked));
    delete stanzaChannel_;
    }*/

    /**
     * Connect using the standard XMPP connection rules (i.e. SRV then A/AAAA).
     * 
     * @param o Client options to use in the connection, must not be null
     */
    public void connect(ClientOptions o) {
        options = o;
        connect(jid_.getDomain(), 5222);
    }

    /**
     * Connect to the specified host, overriding the standard lookup rules for the JID.
     *
     * Internal method, do not use.
     * 
     * @param host Host to connect to, non-null.
     * @param port Default port to use if SRV fails.
     */
    public void connect(String host, int port) {
        options = new ClientOptions();
        disconnectRequested_ = false;
        assert (connector_ == null);
        /* FIXME: Port Proxies */
        connector_ = Connector.create(host, networkFactories.getDomainNameResolver(), networkFactories.getConnectionFactory(), networkFactories.getTimerFactory(), port);
        connectorConnectFinishedConnection_ = connector_.onConnectFinished.connect(new Slot1<Connection>() {
            public void call(Connection p1) {
                handleConnectorFinished(p1);
            }
        });
        connector_.setTimeoutMilliseconds(60 * 1000);
        connector_.start();
    }

    void handleConnectorFinished(Connection connection) {
        if (connectorConnectFinishedConnection_ != null) {
            connectorConnectFinishedConnection_.disconnect();
        }

        connector_ = null;
        if (connection == null) {
            onDisconnected.emit(disconnectRequested_ ? null : new ClientError(ClientError.Type.ConnectionError));
        } else {
            assert (connection_ == null);
            connection_ = connection;

            assert (sessionStream_ == null);
            sessionStream_ = new BasicSessionStream(StreamType.ClientStreamType, connection_, payloadParserFactories_, payloadSerializers_, tlsFactories.getTLSContextFactory(), networkFactories.getTimerFactory(), eventLoop_);
            if (certificate_ != null && !certificate_.isNull()) {
                sessionStream_.setTLSCertificate(certificate_);
            }
            sessionStreamDataReadConnection_ = sessionStream_.onDataRead.connect(new Slot1<String>() {

                public void call(String p1) {
                    handleDataRead(p1);
                }
            });

            sessionStreamDataWrittenConnection_ = sessionStream_.onDataWritten.connect(new Slot1<String>() {

                public void call(String p1) {
                    handleDataWritten(p1);
                }
            });

            session_ = ClientSession.create(jid_, sessionStream_);
            session_.setCertificateTrustChecker(certificateTrustChecker);
            session_.setUseStreamCompression(options.useStreamCompression);
            switch (options.useTLS) {
                case UseTLSWhenAvailable:
                    session_.setUseTLS(ClientSession.UseTLS.UseTLSWhenAvailable);
                    session_.setCertificateTrustChecker(certificateTrustChecker);
                    break;
                case NeverUseTLS:
                    session_.setUseTLS(ClientSession.UseTLS.NeverUseTLS);
                   	break;
            }
            stanzaChannel_.setSession(session_);
            sessionFinishedConnection_ = session_.onFinished.connect(new Slot1<com.isode.stroke.base.Error>() {

                public void call(com.isode.stroke.base.Error p1) {
                    handleSessionFinished(p1);
                }
            });
            sessionNeedCredentialsConnection_ = session_.onNeedCredentials.connect(new Slot() {

                public void call() {
                    handleNeedCredentials();
                }
            });
            session_.start();
        }
    }

    /**
     * Close the stream and disconnect from the server.
     */
    public void disconnect() {
        // FIXME: We should be able to do without this boolean. We just have to make sure we can tell the difference between
        // connector finishing without a connection due to an error or because of a disconnect.
        disconnectRequested_ = true;
        if (session_ != null && !session_.isFinished()) {
            session_.finish();
        } else if (connector_ != null) {
            connector_.stop();
        }
    }

    public void setCertificate(CertificateWithKey certificate) {
        certificate_ = certificate;
    }
    
    /**
     * Sets the certificate trust checker. If a server presents a certificate
     * which does not conform to the requirements of RFC 6120, then the
     * trust checker, if configured, will be called. If the trust checker 
     * says the certificate is trusted, then connecting will proceed; if 
     * not, the connection will end with an error.
     *
     * @param checker a CertificateTrustChecker that will be called when 
     * the server sends a TLS certificate that does not validate. 
     */
    public void setCertificateTrustChecker(CertificateTrustChecker checker) {
        certificateTrustChecker = checker;
    }

    private void handleSessionFinished(com.isode.stroke.base.Error error) {
        sessionFinishedConnection_.disconnect();
        sessionNeedCredentialsConnection_.disconnect();
        session_ = null;

        sessionStreamDataReadConnection_.disconnect();
        sessionStreamDataWrittenConnection_.disconnect();
        sessionStream_ = null;

        connection_.disconnect();
        connection_ = null;

        ClientError clientError = null;
        if (error != null) {
            if (error instanceof ClientSession.Error) {
                ClientSession.Error actualError = (ClientSession.Error) error;
                switch (actualError.type) {
                    case AuthenticationFailedError:
                        clientError = new ClientError(ClientError.Type.AuthenticationFailedError);
                        break;
                    case CompressionFailedError:
                        clientError = new ClientError(ClientError.Type.CompressionFailedError);
                        break;
                    case ServerVerificationFailedError:
                        clientError = new ClientError(ClientError.Type.ServerVerificationFailedError);
                        break;
                    case NoSupportedAuthMechanismsError:
                        clientError = new ClientError(ClientError.Type.NoSupportedAuthMechanismsError);
                        break;
                    case UnexpectedElementError:
                        clientError = new ClientError(ClientError.Type.UnexpectedElementError);
                        break;
                    case ResourceBindError:
                        clientError = new ClientError(ClientError.Type.ResourceBindError);
                        break;
                    case SessionStartError:
                        clientError = new ClientError(ClientError.Type.SessionStartError);
                        break;
                    case TLSError:
                        clientError = new ClientError(ClientError.Type.TLSError);
                        break;
                    case TLSClientCertificateError:
                        clientError = new ClientError(ClientError.Type.ClientCertificateError);
                        break;
                    /* Note: no case clause for "StreamError" */
                }
            } else if (error instanceof SessionStream.Error) {
                SessionStream.Error actualError = (SessionStream.Error) error;
                switch (actualError.type) {
                    case ParseError:
                        clientError = new ClientError(ClientError.Type.XMLError);
                        break;
                    case TLSError:
                        clientError = new ClientError(ClientError.Type.TLSError);
                        break;
                    case InvalidTLSCertificateError:
                        clientError = new ClientError(ClientError.Type.ClientCertificateLoadError);
                        break;
                    case ConnectionReadError:
                        clientError = new ClientError(ClientError.Type.ConnectionReadError);
                        break;
                    case ConnectionWriteError:
                        clientError = new ClientError(ClientError.Type.ConnectionWriteError);
                        break;
                }
            } else if (error instanceof CertificateVerificationError) {
                CertificateVerificationError verificationError = (CertificateVerificationError)error;
                switch (verificationError.type) {
                    case UnknownError:
                        clientError = new ClientError(ClientError.Type.UnknownCertificateError);
                        break;
                    case Expired:
                        clientError = new ClientError(ClientError.Type.CertificateExpiredError);
                        break;
                    case NotYetValid:
                        clientError = new ClientError(ClientError.Type.CertificateNotYetValidError);
                        break;
                    case SelfSigned:
                        clientError = new ClientError(ClientError.Type.CertificateSelfSignedError);
                        break;
                    case Rejected:
                        clientError = new ClientError(ClientError.Type.CertificateRejectedError);
                        break;
                    case Untrusted:
                        clientError = new ClientError(ClientError.Type.CertificateUntrustedError);
                        break;
                    case InvalidPurpose:
                        clientError = new ClientError(ClientError.Type.InvalidCertificatePurposeError);
                        break;
                    case PathLengthExceeded:
                        clientError = new ClientError(ClientError.Type.CertificatePathLengthExceededError);
                        break;
                    case InvalidSignature:
                        clientError = new ClientError(ClientError.Type.InvalidCertificateSignatureError);
                        break;
                    case InvalidCA:
                        clientError = new ClientError(ClientError.Type.InvalidCAError);
                        break;
                    case InvalidServerIdentity:
                        clientError = new ClientError(ClientError.Type.InvalidServerIdentityError);
                        break;
                }
            }
            /* If "error" was non-null, we expect to be able to derive 
             * a non-null "clientError".
             */  
            NotNull.exceptIfNull(clientError,"clientError");
        }
        onDisconnected.emit(clientError);
    }

    private void handleNeedCredentials() {
        assert session_ != null;
        session_.sendCredentials(password_);
    }

    private void handleDataRead(String data) {
        onDataRead.emit(data);
    }

    private void handleDataWritten(String data) {
        onDataWritten.emit(data);
    }

    private void handleStanzaChannelAvailableChanged(boolean available) {
        if (available) {
            onConnected.emit();
        }
    }

    public void sendMessage(Message message) {
        stanzaChannel_.sendMessage(message);
    }

    public void sendPresence(Presence presence) {
        stanzaChannel_.sendPresence(presence);
    }

    /**
     * Get the IQRouter responsible for all IQs on this connection.
     * Use this to send IQs.
     */
    public IQRouter getIQRouter() {
        return iqRouter_;
    }

    public StanzaChannel getStanzaChannel() {
        return stanzaChannel_;
    }

    /**
     * @return session is available for sending/receiving stanzas.
     */
    public boolean isAvailable() {
        return stanzaChannel_.isAvailable();
    }
    
    /**
     * Determine whether the underlying session is encrypted with TLS
     * @return true if the session is initialized and encrypted with TLS,
     * false otherwise.
     */
    public boolean isSessionTLSEncrypted() {
        return (sessionStream_ != null && sessionStream_.isTLSEncrypted());
    }
    
    /**
     * If the session is initialized and encrypted with TLS, then the
     * certificate presented by the peer is returned
     * @return the peer certificate, if one is available, otherwise null.
     */
    public Certificate getSessionCertificate() {
        return (isSessionTLSEncrypted() ? sessionStream_.getPeerCertificate() : null);
    }


    /**
     * @return JID of the client, will never be null. After the session was
     *         initialized, this returns the bound JID (the JID provided by
     *         the server during resource binding). Prior to this it returns 
     *         the JID provided by the user.
     */
    public JID getJID() {
        if (session_ != null) {
            return session_.getLocalJID();
        } else {
            return jid_;
        }
    }
    
    @Override
    public String toString()
    {
        return "CoreClient for \"" + jid_ + "\"" +
        "; session " + (isAvailable() ? "" : "un") + "available"; 
    }

    /**
     * The user should add a listener to this signal, which will be called when
     * the client was disconnected from tne network.
     * 
     * <p>If the disconnection was due to a non-recoverable error, the type
     * of error will be passed as a parameter.
     */
    public final Signal1<ClientError> onDisconnected = new Signal1<ClientError>();

    /**
     * The user should add a listener to this signal, which will be called when
     * the connection is established with the server.
     */
    public final Signal onConnected = new Signal();

    /**
     * The user may add a listener to this signal, which will be called when
     * data are received from the server. Useful for observing protocol exchange.
     */
    public final Signal1<String> onDataRead = new Signal1<String>();

    /**
     * The user may add a listener to this signal, which will be called when
     * data are sent to the server. Useful for observing protocol exchange.
     */
    public final Signal1<String> onDataWritten = new Signal1<String>();

    /**
     * Called when a message stanza is received.
     */
    public final Signal1<Message> onMessageReceived = new Signal1<Message>();
    /**
     * Called when a presence stanza is received.
     */
    public final Signal1<Presence> onPresenceReceived = new Signal1<Presence>();
    /**
     * Called when a stanza has been received and acked by a server supporting XEP-0198.
     */
    public final Signal1<Stanza> onStanzaAcked = new Signal1<Stanza>();
    private PlatformDomainNameResolver resolver_;
    private JID jid_;
    private String password_;
    private ClientSessionStanzaChannel stanzaChannel_;
    private IQRouter iqRouter_;
    private Connector connector_;
    private ConnectionFactory connectionFactory_;
    private FullPayloadParserFactoryCollection payloadParserFactories_ = new FullPayloadParserFactoryCollection();
    private FullPayloadSerializerCollection payloadSerializers_ = new FullPayloadSerializerCollection();
    private Connection connection_;
    private BasicSessionStream sessionStream_;
    private ClientSession session_;
    private CertificateWithKey certificate_;
    private boolean disconnectRequested_;
    private ClientOptions options;
    private CertificateTrustChecker certificateTrustChecker;
    private NetworkFactories networkFactories;
    private PlatformTLSFactories tlsFactories;
}
