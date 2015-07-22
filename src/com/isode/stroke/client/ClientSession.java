/*
 * Copyright (c) 2010-2015 Isode Limited, London, England.
 * All rights reserved.
 */
/*
 * Copyright (c) 2010-2014 Remko Tronçon.
 * All rights reserved.
 */
package com.isode.stroke.client;

import com.isode.stroke.base.ByteArray;
import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.elements.AuthChallenge;
import com.isode.stroke.elements.AuthFailure;
import com.isode.stroke.elements.AuthRequest;
import com.isode.stroke.elements.AuthResponse;
import com.isode.stroke.elements.AuthSuccess;
import com.isode.stroke.elements.CompressFailure;
import com.isode.stroke.elements.CompressRequest;
import com.isode.stroke.elements.Compressed;
import com.isode.stroke.elements.Element;
import com.isode.stroke.elements.EnableStreamManagement;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.elements.ProtocolHeader;
import com.isode.stroke.elements.ResourceBind;
import com.isode.stroke.elements.Stanza;
import com.isode.stroke.elements.StanzaAck;
import com.isode.stroke.elements.StanzaAckRequest;
import com.isode.stroke.elements.StartSession;
import com.isode.stroke.elements.StartTLSFailure;
import com.isode.stroke.elements.StreamFeatures;
import com.isode.stroke.elements.StartTLSRequest;
import com.isode.stroke.elements.StreamError;
import com.isode.stroke.elements.StreamManagementEnabled;
import com.isode.stroke.elements.StreamManagementFailed;
import com.isode.stroke.elements.TLSProceed;
import com.isode.stroke.jid.JID;
import com.isode.stroke.sasl.ClientAuthenticator;
import com.isode.stroke.sasl.PLAINClientAuthenticator;
import com.isode.stroke.sasl.SCRAMSHA1ClientAuthenticator;
import com.isode.stroke.session.SessionStream;
import com.isode.stroke.signals.Signal;
import com.isode.stroke.signals.Signal1;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.streammanagement.StanzaAckRequester;
import com.isode.stroke.streammanagement.StanzaAckResponder;
import com.isode.stroke.tls.Certificate;
import com.isode.stroke.tls.CertificateTrustChecker;
import com.isode.stroke.tls.CertificateVerificationError;
import com.isode.stroke.tls.ServerIdentityVerifier;
import com.isode.stroke.idn.IDNConverter;
import com.isode.stroke.idn.ICUConverter;
import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.crypto.JavaCryptoProvider;

import java.util.List;
import java.util.UUID;

public class ClientSession {
    public final Signal onNeedCredentials = new Signal();
    public final Signal onInitialized = new Signal();
    public final Signal1<com.isode.stroke.base.Error> onFinished = new Signal1<com.isode.stroke.base.Error>();
    public final Signal1<Stanza> onStanzaReceived = new Signal1<Stanza>();
    public final Signal1<Stanza> onStanzaAcked = new Signal1<Stanza>();

    private SignalConnection streamElementReceivedConnection;
    private SignalConnection streamStreamStartReceivedConnection;
    private SignalConnection streamClosedConnection;
    private SignalConnection streamTLSEncryptedConnection;
    private SignalConnection stanzaAckOnRequestConnection_;
    private SignalConnection stanzaAckOnAckedConnection_;
    private SignalConnection stanzaResponderAckConnection_;
    private JID localJID;
    private State state;
    private final SessionStream stream;
    private boolean allowPLAINOverNonTLS;
    private boolean useStreamCompression;
    private UseTLS useTLS;
    private boolean useAcks;
    private boolean needSessionStart;
    private boolean needResourceBind;
    private boolean needAcking;
    private boolean rosterVersioningSupported;
    private ClientAuthenticator authenticator;
    private StanzaAckRequester stanzaAckRequester_;
    private StanzaAckResponder stanzaAckResponder_;
    private com.isode.stroke.base.Error error_;
    private CertificateTrustChecker certificateTrustChecker;
    private IDNConverter idnConverter = new ICUConverter(); //TOPORT: Accomodated later in Constructor.
    private CryptoProvider crypto = new JavaCryptoProvider(); //TOPORT: Accomodated later in Constructor.

    public enum State {

        Initial,
        WaitingForStreamStart,
        Negotiating,
        Compressing,
        WaitingForEncrypt,
        Encrypting,
        WaitingForCredentials,
        Authenticating,
        EnablingSessionManagement,
        BindingResource,
        StartingSession,
        Initialized,
        Finishing,
        Finished
    };

    public static class Error implements com.isode.stroke.base.Error {
        public final Type type;
        public enum Type {

            AuthenticationFailedError,
            CompressionFailedError,
            ServerVerificationFailedError,
            NoSupportedAuthMechanismsError,
            UnexpectedElementError,
            ResourceBindError,
            SessionStartError,
            TLSClientCertificateError,
            TLSError,
            StreamError
        };

        public Error(final Type type) {
            if (type == null) {
                throw new IllegalStateException();
            }
            this.type = type;
        }
    };

    public enum UseTLS {
        NeverUseTLS,
        UseTLSWhenAvailable,
        RequireTLS
    }

    private ClientSession(final JID jid, final SessionStream stream) {
        localJID = jid;
        state = State.Initial;
        this.stream = stream;
        allowPLAINOverNonTLS = false;
        useStreamCompression = true;
        useTLS = UseTLS.UseTLSWhenAvailable;
        useAcks = true;
        needSessionStart = false;
        needResourceBind = false;
        needAcking = false;
        authenticator = null;
    }

    public static ClientSession create(final JID jid, final SessionStream stream) {
        return new ClientSession(jid, stream);
    }

    public State getState() {
        return state;
    }

    public void setAllowPLAINOverNonTLS(final boolean b) {
        allowPLAINOverNonTLS = b;
    }

    public void setUseStreamCompression(final boolean b) {
        useStreamCompression = b;
    }

    public void setUseTLS(final UseTLS use) {
        useTLS = use;
    }

    public void setUseAcks(final boolean use) {
        useAcks = use;
    }

    public boolean getStreamManagementEnabled() {
        return stanzaAckRequester_ != null;
    }

    public boolean getRosterVersioningSuported() {
        return rosterVersioningSupported;
    }
    
    public List<Certificate> getPeerCertificateChain() {
        return stream.getPeerCertificateChain();
    }

    public JID getLocalJID() {
        return localJID;
    }

    public boolean isFinished() {
        return State.Finished.equals(getState());
    }

    public void setCertificateTrustChecker(final CertificateTrustChecker checker) {
        certificateTrustChecker = checker;
    }

    public void start() {
        streamStreamStartReceivedConnection = stream.onStreamStartReceived.connect(new Slot1<ProtocolHeader>(){
            public void call(final ProtocolHeader p1) {
                handleStreamStart(p1);
            }
        });
	streamElementReceivedConnection = stream.onElementReceived.connect(new Slot1<Element>(){
            public void call(final Element p1) {
                handleElement(p1);
            }
        });
	streamClosedConnection = stream.onClosed.connect(new Slot1<SessionStream.Error>(){
            public void call(final SessionStream.Error p1) {
                handleStreamClosed(p1);
            }
        });
	streamTLSEncryptedConnection = stream.onTLSEncrypted.connect(new Slot(){
            public void call() {
                handleTLSEncrypted();
            }
        });

	assert state.equals(State.Initial);
	state = State.WaitingForStreamStart;
	sendStreamHeader();
    }

    private void sendStreamHeader() {
        final ProtocolHeader header = new ProtocolHeader();
	header.setTo(getRemoteJID().toString());
	stream.writeHeader(header);
    }

    public void sendStanza(final Stanza stanza) {
        stream.writeElement(stanza);
	if (stanzaAckRequester_ != null) {
            stanzaAckRequester_.handleStanzaSent(stanza);
	}
    }

    private void handleStreamStart(final ProtocolHeader header) { //NOPMD, I'm not removing header from the API
        if (!checkState(State.WaitingForStreamStart)) {
            return;
        }
	state = State.Negotiating;
    }

    private void handleElement(final Element element) {
       	if (element instanceof Stanza) {
            final Stanza stanza = (Stanza) element;
            if (stanzaAckResponder_ != null) {
                stanzaAckResponder_.handleStanzaReceived();
            }
	    if (getState().equals(State.Initialized)) {
                onStanzaReceived.emit(stanza);
            }
            else if (stanza instanceof IQ) {
                final IQ iq = (IQ)stanza;
                if (getState().equals(State.BindingResource)) {
                    final ResourceBind resourceBind = iq.getPayload(new ResourceBind());
                    if (IQ.Type.Error.equals(iq.getType()) && iq.getID().equals("session-bind")) {
                        finishSession(Error.Type.ResourceBindError);
                    }
                    else if (resourceBind == null) {
                        finishSession(Error.Type.UnexpectedElementError);
                    }
                    else if (IQ.Type.Result.equals(iq.getType())) {
                        localJID = resourceBind.getJID();
                        if (!localJID.isValid()) {
                            finishSession(Error.Type.ResourceBindError);
                        }
                        needResourceBind = false;
                        continueSessionInitialization();
                    }
                    else {
                        finishSession(Error.Type.UnexpectedElementError);
                    }
                }
		else if (state.equals(State.StartingSession)) {
                    if (IQ.Type.Result.equals(iq.getType())) {
                        needSessionStart = false;
                        continueSessionInitialization();
                    }
                    else if (IQ.Type.Error.equals(iq.getType())) {
                        finishSession(Error.Type.SessionStartError);
                    }
                    else {
                        finishSession(Error.Type.UnexpectedElementError);
                    }
                }
                else {
                    finishSession(Error.Type.UnexpectedElementError);
                }
            }
        }
        else if (element instanceof StanzaAckRequest) {
            if (stanzaAckResponder_ != null) {

                stanzaAckResponder_.handleAckRequestReceived();
            }
        }
        else if (element instanceof StanzaAck) {
            final StanzaAck ack = (StanzaAck) element;
            if (stanzaAckRequester_ != null) {
                if (ack.isValid()) {
                    stanzaAckRequester_.handleAckReceived(ack.getHandledStanzasCount());
                }
                //else {
                    //logger_.warning("Got invalid ack from server"); /*FIXME: Do we want logging here?
                //}
            }
            //else {
                //logger_.warning("Ignoring ack"); /*FIXME: Do we want logging here?*/
            //}
        }
        else if (element instanceof StreamError) {
            finishSession(Error.Type.StreamError);
        }
	else if (State.Initialized.equals(getState())) {
            final Stanza stanza = element instanceof Stanza ? (Stanza)element : null;
            if (stanza != null) {
                if (stanzaAckResponder_ != null) {
                    stanzaAckResponder_.handleStanzaReceived();
                }
                onStanzaReceived.emit(stanza);
            }
        }
        else if (element instanceof StreamFeatures) {
            if (!checkState(State.Negotiating)) {
                return;
            }
            
            final StreamFeatures streamFeatures = (StreamFeatures) element;
            if (streamFeatures.hasStartTLS() && stream.supportsTLSEncryption() && !UseTLS.NeverUseTLS.equals(useTLS)) {
                state = State.WaitingForEncrypt;
                stream.writeElement(new StartTLSRequest());
            }
            else if (UseTLS.RequireTLS.equals(useTLS) && !stream.isTLSEncrypted()) {
                finishSession(Error.Type.NoSupportedAuthMechanismsError);
            }
            else if (useStreamCompression && streamFeatures.hasCompressionMethod("zlib")) {
                state = State.Compressing;
                stream.writeElement(new CompressRequest("zlib"));
            }
            else if (streamFeatures.hasAuthenticationMechanisms()) {
                if (stream.hasTLSCertificate()) {
                    if (streamFeatures.hasAuthenticationMechanism("EXTERNAL")) {
                        state = State.Authenticating;
                        stream.writeElement(new AuthRequest("EXTERNAL",new SafeByteArray()));
                    }
                    else {
                        finishSession(Error.Type.TLSClientCertificateError);
                    }
                }
                else if (streamFeatures.hasAuthenticationMechanism("EXTERNAL")) {
                    state = State.Authenticating;
                    stream.writeElement(new AuthRequest("EXTERNAL",new SafeByteArray()));
                }
                else if (streamFeatures.hasAuthenticationMechanism("SCRAM-SHA-1") || streamFeatures.hasAuthenticationMechanism("SCRAM-SHA-1-PLUS")) {
                    final SCRAMSHA1ClientAuthenticator scramAuthenticator = new SCRAMSHA1ClientAuthenticator(UUID.randomUUID().toString(), streamFeatures.hasAuthenticationMechanism("SCRAM-SHA-1-PLUS"), idnConverter, crypto);
                    if (stream.isTLSEncrypted()) {
                        scramAuthenticator.setTLSChannelBindingData(stream.getTLSFinishMessage());
                    }
                    authenticator = scramAuthenticator;
                    state = State.WaitingForCredentials;
                    onNeedCredentials.emit();
                }
                else if ((stream.isTLSEncrypted() || allowPLAINOverNonTLS) && streamFeatures.hasAuthenticationMechanism("PLAIN")) {
                        authenticator = new PLAINClientAuthenticator();
                        state = State.WaitingForCredentials;
                        onNeedCredentials.emit();
                }
//                        //FIXME: Port
//			else if (streamFeatures.hasAuthenticationMechanism("DIGEST-MD5")) {
//				// FIXME: Host should probably be the actual host
//				authenticator = new DIGESTMD5ClientAuthenticator(localJID.getDomain(), UUID.randomUUID());
//				state = State.WaitingForCredentials;
//				onNeedCredentials.emit();
//			}
                else {
                        finishSession(Error.Type.NoSupportedAuthMechanismsError);
                }
            }
            else {
                // Start the session
                rosterVersioningSupported = streamFeatures.hasRosterVersioning();
                stream.setWhitespacePingEnabled(true);
                needSessionStart = streamFeatures.hasSession();
                needResourceBind = streamFeatures.hasResourceBind();
                needAcking = streamFeatures.hasStreamManagement() && useAcks;
                if (!needResourceBind) {
                    // Resource binding is a MUST
                    finishSession(Error.Type.ResourceBindError);
                } else {
                    continueSessionInitialization();
                }
            }
	}
        else if (element instanceof Compressed) {
            checkState(State.Compressing);
            state = State.WaitingForStreamStart;
            stream.addZLibCompression();
            stream.resetXMPPParser();
            sendStreamHeader();
        }
        else if (element instanceof CompressFailure) {
            finishSession(Error.Type.CompressionFailedError);
        }
        else if (element instanceof StreamManagementEnabled) {
            stanzaAckRequester_ = new StanzaAckRequester();
            stanzaAckOnRequestConnection_ = stanzaAckRequester_.onRequestAck.connect(new Slot() {

                public void call() {
                    requestAck();
                }
            });
            stanzaAckOnAckedConnection_ = stanzaAckRequester_.onStanzaAcked.connect(new Slot1<Stanza>() {

                public void call(final Stanza p1) {
                    handleStanzaAcked(p1);
                }
            });
            stanzaAckResponder_ = new StanzaAckResponder();
            stanzaResponderAckConnection_ = stanzaAckResponder_.onAck.connect(new Slot1<Long>() {

                public void call(final Long p1) {
                    ack(p1);
                }
            });
            needAcking = false;
            continueSessionInitialization();
        }
        else if (element instanceof StreamManagementFailed) {
            needAcking = false;
            continueSessionInitialization();
        }
        else if (element instanceof AuthChallenge) {
            final AuthChallenge challenge = (AuthChallenge) element;
            checkState(State.Authenticating);
            assert authenticator != null;
            if (authenticator.setChallenge(challenge.getValue())) {
                stream.writeElement(new AuthResponse(authenticator.getResponse()));
            }
            else {
                finishSession(Error.Type.AuthenticationFailedError);
            }
	}
	else if (element instanceof AuthSuccess) {
            final AuthSuccess authSuccess = (AuthSuccess)element;
            checkState(State.Authenticating);
            if (authenticator != null && !authenticator.setChallenge(authSuccess.getValue())) {
                finishSession(Error.Type.ServerVerificationFailedError);
            }
            else {
                state = State.WaitingForStreamStart;
                authenticator = null;
                stream.resetXMPPParser();
                sendStreamHeader();
            }
	}
	else if (element instanceof AuthFailure) {
		authenticator = null;
		finishSession(Error.Type.AuthenticationFailedError);
	}
	else if (element instanceof TLSProceed) {
            if (!checkState(State.WaitingForEncrypt)) {
                return;
            }
            state = State.Encrypting;
            stream.addTLSEncryption();
	}
	else if (element instanceof StartTLSFailure) {
            finishSession(Error.Type.TLSError);
	}
	else {
            // FIXME Not correct?
            state = State.Initialized;
            onInitialized.emit();
	}
    }

    private void continueSessionInitialization() {
        if (needResourceBind) {
            state = State.BindingResource;
            final ResourceBind resourceBind = new ResourceBind();
            if (localJID.getResource().length() != 0) {
                resourceBind.setResource(localJID.getResource());
            }
            sendStanza(IQ.createRequest(IQ.Type.Set, new JID(), "session-bind", resourceBind));
        }
        else if (needAcking) {
            state = State.EnablingSessionManagement;
            stream.writeElement(new EnableStreamManagement());
        }
        else if (needSessionStart) {
            state = State.StartingSession;
            sendStanza(IQ.createRequest(IQ.Type.Set, new JID(), "session-start", new StartSession()));
        }
        else {
            state = State.Initialized;
            onInitialized.emit();
        }
    }

    private boolean checkState(final State state) {
        final State currentState = this.state; /* For symbol debugging, as the following overwrites it */
        if (!currentState.equals(state)) {
            finishSession(Error.Type.UnexpectedElementError);
            return false;
        }
        return true;
    }

    public void sendCredentials(final SafeByteArray password) {
        if (!checkState(State.WaitingForCredentials)) {
            throw new IllegalStateException("Asking for credentials when we shouldn't be asked.");
        }
	state = State.Authenticating;
	authenticator.setCredentials(localJID.getNode(), password);
	stream.writeElement(new AuthRequest(authenticator.getName(), authenticator.getResponse()));
    }

    private void handleTLSEncrypted() {
        if (!checkState(State.Encrypting)) {
            return;
        }
        final List<Certificate> certificateChain = stream.getPeerCertificateChain();
        final Certificate peerCertificate = 
                (certificateChain == null || certificateChain.isEmpty() ? null : certificateChain.get(0));
        final CertificateVerificationError verificationError = stream.getPeerCertificateVerificationError();
        if (verificationError != null) {
            checkTrustOrFinish(certificateChain, verificationError);
        }
        else {
            final ServerIdentityVerifier identityVerifier = new ServerIdentityVerifier(localJID);
            if (identityVerifier.certificateVerifies(peerCertificate)) {
                continueAfterTLSEncrypted();
            }
            else {
                checkTrustOrFinish(certificateChain, new CertificateVerificationError(CertificateVerificationError.Type.InvalidServerIdentity));
            }
        }
    }

    private void checkTrustOrFinish(final List<Certificate> certificateChain, final CertificateVerificationError error) {
        if (certificateTrustChecker != null && certificateTrustChecker.isCertificateTrusted(certificateChain)) {
            continueAfterTLSEncrypted();
        }
        else {
            finishSession(error);
        }
    }

    private void continueAfterTLSEncrypted() {
        state = State.WaitingForStreamStart;
        stream.resetXMPPParser();
        sendStreamHeader();
    }

    private void handleStreamClosed(final SessionStream.Error streamError) {
	final State previousState = state;
	state = State.Finished;

	if (stanzaAckRequester_ != null) {
		stanzaAckOnRequestConnection_.disconnect();
		stanzaAckOnAckedConnection_.disconnect();
		stanzaAckRequester_ = null;
	}
	if (stanzaAckResponder_ != null) {
		stanzaResponderAckConnection_.disconnect();
		stanzaAckResponder_ = null;
	}
	stream.setWhitespacePingEnabled(false);
      	streamStreamStartReceivedConnection.disconnect();
	streamElementReceivedConnection.disconnect();
	streamClosedConnection.disconnect();
	streamTLSEncryptedConnection.disconnect();

	if (State.Finishing.equals(previousState)) {
            onFinished.emit(error_);
	}
	else {
            onFinished.emit(streamError);
	}
    }

    public void finish() {
        finishSession((Error.Type)null);
    }

    private void finishSession(final Error.Type error) {
        Error localError = null;
        if (error != null) {
            localError = new Error(error);
        }
        finishSession(localError);
    }

    private void finishSession(final com.isode.stroke.base.Error error) {
        state = State.Finishing;
	error_ = error;
	assert stream.isOpen();
	if (stanzaAckResponder_ != null) {
            stanzaAckResponder_.handleAckRequestReceived();
	}
	stream.writeFooter();
	stream.close();
    }

    private void requestAck() {
        stream.writeElement(new StanzaAckRequest());
    }

    private void handleStanzaAcked(final Stanza stanza) {
        onStanzaAcked.emit(stanza);
    }

    private void ack(final long handledStanzasCount) {
        stream.writeElement(new StanzaAck(handledStanzasCount));
    }

    private JID getRemoteJID() {
        return new JID("", localJID.getDomain());
    }
    
    @Override
    public String toString() {
        return "useTLS=" + useTLS + 
        "; allowPLAINOverNonTLS=" + allowPLAINOverNonTLS +
        "; certificatetrustChecker=" + certificateTrustChecker +
        "; state=" + state +
        "; error_=" + error_;
    }


}
