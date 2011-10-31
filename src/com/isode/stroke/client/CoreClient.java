/*
 * Copyright (c) 2010, Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010, Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.client;

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
import com.isode.stroke.network.TimerFactory;
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
import com.isode.stroke.tls.CertificateTrustChecker;
import com.isode.stroke.tls.CertificateVerificationError;
import com.isode.stroke.tls.PKCS12Certificate;
import com.isode.stroke.tls.PlatformTLSFactories;
import com.isode.stroke.tls.TLSContextFactory;

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
    public void connect(ClientOptions o) {
        options = o;
        connect(jid_.getDomain());
    }

    public void connect(String host) {
        disconnectRequested_ = false;
        assert (connector_ == null);
        /* FIXME: Port Proxies */
        connector_ = Connector.create(host, networkFactories.getDomainNameResolver(), networkFactories.getConnectionFactory(), networkFactories.getTimerFactory());
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
            if (!disconnectRequested_) {
                onError.emit(new ClientError(ClientError.Type.ConnectionError));
            }
        } else {
            assert (connection_ == null);
            connection_ = connection;

            assert (sessionStream_ == null);
            sessionStream_ = new BasicSessionStream(StreamType.ClientStreamType, connection_, payloadParserFactories_, payloadSerializers_, tlsFactories.getTLSContextFactory(), networkFactories.getTimerFactory(), eventLoop_);
            if (certificate_ != null && !certificate_.isEmpty()) {
                sessionStream_.setTLSCertificate(new PKCS12Certificate(certificate_, password_));
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

    public void setCertificate(String certificate) {
        certificate_ = certificate;
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

        if (error != null) {
            ClientError clientError = null;
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
            assert clientError != null;
            onError.emit(clientError);
        }
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
     * Returns the JID of the client.
     * After the session was initialized, this returns the bound JID.
     */
    public JID getJID() {
        if (session_ != null) {
            return session_.getLocalJID();
        } else {
            return jid_;
        }
    }
    public final Signal1<ClientError> onError = new Signal1<ClientError>();
    public final Signal onConnected = new Signal();
    public final Signal1<String> onDataRead = new Signal1<String>();
    public final Signal1<String> onDataWritten = new Signal1<String>();
    public final Signal1<Message> onMessageReceived = new Signal1<Message>();
    public final Signal1<Presence> onPresenceReceived = new Signal1<Presence>();
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
    private String certificate_;
    private boolean disconnectRequested_;
    private ClientOptions options;
    private CertificateTrustChecker certificateTrustChecker;
    private NetworkFactories networkFactories;
    private PlatformTLSFactories tlsFactories;
}
