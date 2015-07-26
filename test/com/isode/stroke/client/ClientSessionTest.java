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

package com.isode.stroke.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Before;
import com.isode.stroke.base.ByteArray;
import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.session.SessionStream;
import com.isode.stroke.elements.AuthChallenge;
import com.isode.stroke.elements.AuthFailure;
import com.isode.stroke.elements.AuthRequest;
import com.isode.stroke.elements.Message;
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
import com.isode.stroke.sasl.DIGESTMD5ClientAuthenticator;
import com.isode.stroke.sasl.EXTERNALClientAuthenticator;
import com.isode.stroke.session.SessionStream;
import com.isode.stroke.client.ClientSession;
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
import com.isode.stroke.tls.SimpleCertificate;
import com.isode.stroke.tls.BlindCertificateTrustChecker;
import com.isode.stroke.idn.IDNConverter;
import com.isode.stroke.idn.ICUConverter;
import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.crypto.JavaCryptoProvider;
import java.util.logging.Logger;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.Collection;

public class ClientSessionTest {

	private IDNConverter idnConverter;
	private MockSessionStream server;
	private boolean sessionFinishedReceived;
	private boolean needCredentials;
	private com.isode.stroke.base.Error sessionFinishedError;
	private BlindCertificateTrustChecker blindCertificateTrustChecker;
	private CryptoProvider crypto;

	private class MockSessionStream extends SessionStream {

		class Event {
			public Event(Element element) {
				this.element = element;
				this.footer = false;
			}
			public Event(final ProtocolHeader header) {
				this.header = header;
				this.footer = false;
			}
			public Event() {
				this.footer = true;
			}

			public Element element;
			public ProtocolHeader header;
			public boolean footer;
		};

		public MockSessionStream() {
			this.available = true;
			this.canTLSEncrypt = true;
			this.tlsEncrypted = false;
			this.compressed = false;
			this.whitespacePingEnabled = false;
			this.resetCount = 0;
		}

		public void disconnect() {

		}

		public void close() {
			onClosed.emit((com.isode.stroke.base.Error)null);
		}

		public boolean isOpen() {
			return available;
		}

		public void writeHeader(final ProtocolHeader header) {
			receivedEvents.add(new Event(header));
		}

		public void writeFooter() {
			receivedEvents.add(new Event());
		}

		public void writeElement(Element element) {
			receivedEvents.add(new Event(element));
		}

		public void writeData(final String data) {
		}

		public boolean supportsTLSEncryption() {
			return canTLSEncrypt;
		}

		public void addTLSEncryption() {
			tlsEncrypted = true;
		}

		public boolean isTLSEncrypted() {
			return tlsEncrypted;
		}

		public ByteArray getTLSFinishMessage() {
			return new ByteArray();
		}

		public Certificate getPeerCertificate() {
			return new SimpleCertificate();
		}

		public Vector<Certificate> getPeerCertificateChain() {
			return new Vector<Certificate>();
		}

		public CertificateVerificationError getPeerCertificateVerificationError() {
			return null;
		}

		public boolean supportsZLibCompression() {
			return true;
		}
		
		public void addZLibCompression() {
			compressed = true;
		}

		public void setWhitespacePingEnabled(boolean enabled) {
			whitespacePingEnabled = enabled;
		}

		public void resetXMPPParser() {
			resetCount++;
		}

		public void breakConnection() {
			onClosed.emit(new SessionStream.SessionStreamError(SessionStream.SessionStreamError.Type.ConnectionReadError));
		}

		public void breakTLS() {
			onClosed.emit(new SessionStream.SessionStreamError(SessionStream.SessionStreamError.Type.TLSError));
		}


		public void sendStreamStart() {
			ProtocolHeader header = new ProtocolHeader();
			header.setTo("foo.com");
			onStreamStartReceived.emit(header);
		}

		public void sendStreamFeaturesWithStartTLS() {
			StreamFeatures streamFeatures = new StreamFeatures();
			streamFeatures.setHasStartTLS();
			onElementReceived.emit(streamFeatures);
		}

		public void sendChallenge() {
			onElementReceived.emit(new AuthChallenge());
		}

		public void sendStreamError() {
			onElementReceived.emit(new StreamError());
		}

		public void sendTLSProceed() {
			onElementReceived.emit(new TLSProceed());
		}

		public void sendTLSFailure() {
			onElementReceived.emit(new StartTLSFailure());
		}

		public void sendStreamFeaturesWithMultipleAuthentication() {
			StreamFeatures streamFeatures = new StreamFeatures();
			streamFeatures.addAuthenticationMechanism("PLAIN");
			streamFeatures.addAuthenticationMechanism("DIGEST-MD5");
			streamFeatures.addAuthenticationMechanism("SCRAM-SHA1");
			onElementReceived.emit(streamFeatures);
		}

		public void sendStreamFeaturesWithPLAINAuthentication() {
			StreamFeatures streamFeatures = new StreamFeatures();
			streamFeatures.addAuthenticationMechanism("PLAIN");
			onElementReceived.emit(streamFeatures);
		}

		public void sendStreamFeaturesWithEXTERNALAuthentication() {
			StreamFeatures streamFeatures = new StreamFeatures();
			streamFeatures.addAuthenticationMechanism("EXTERNAL");
			onElementReceived.emit(streamFeatures);
		}

		public void sendStreamFeaturesWithUnknownAuthentication() {
			StreamFeatures streamFeatures = new StreamFeatures();
			streamFeatures.addAuthenticationMechanism("UNKNOWN");
			onElementReceived.emit(streamFeatures);
		}

		public void sendStreamFeaturesWithBindAndStreamManagement() {
			StreamFeatures streamFeatures = new StreamFeatures();
			streamFeatures.setHasResourceBind();
			streamFeatures.setHasStreamManagement();
			onElementReceived.emit(streamFeatures);
		}

		public void sendEmptyStreamFeatures() {
			onElementReceived.emit(new StreamFeatures());
		}

		public void sendAuthSuccess() {
			onElementReceived.emit(new AuthSuccess());
		}

		public void sendAuthFailure() {
			onElementReceived.emit(new AuthFailure());
		}

		public void sendStreamManagementEnabled() {
			onElementReceived.emit(new StreamManagementEnabled());
		}

		public void sendStreamManagementFailed() {
			onElementReceived.emit(new StreamManagementFailed());
		}

		public void sendBindResult() {
			ResourceBind resourceBind = new ResourceBind();
			resourceBind.setJID(new JID("foo@bar.com/bla"));
			IQ iq = IQ.createResult(new JID("foo@bar.com"), bindID, resourceBind);
			onElementReceived.emit(iq);
		}

		public void sendMessage() {
			Message message = new Message();
			message.setTo(new JID("foo@bar.com/bla"));
			onElementReceived.emit(message);
		}

		public void receiveStreamStart() {
			Event event = popEvent();
			assertNotNull(event.header);
		}

		public void receiveStartTLS() {
			Event event = popEvent();
			assertNotNull(event.element);
			assertTrue(event.element instanceof StartTLSRequest);
		}

		public void receiveAuthRequest(final String mech) {
			Event event = popEvent();
			assertNotNull(event.element);
			AuthRequest request = (AuthRequest)(event.element);
			assertNotNull(request);
			assertEquals(mech, request.getMechanism());
		}

		public void receiveStreamManagementEnable() {
			Event event = popEvent();
			assertNotNull(event.element);
			assertTrue(event.element instanceof EnableStreamManagement);
		}

		public void receiveBind() {
			Event event = popEvent();
			assertNotNull(event.element);
			IQ iq = (IQ)(event.element);
			assertNotNull(iq);
			assertNotNull(iq.getPayload(new ResourceBind()));
			bindID = iq.getID();
		}

		public void receiveAck(int n) {
			Event event = popEvent();
			assertNotNull(event.element);
			StanzaAck ack = (StanzaAck)(event.element);
			assertNotNull(ack);
			assertEquals(n, ack.getHandledStanzasCount());
		}

		public Event popEvent() {
			assertFalse(receivedEvents.isEmpty());
			Event event = receivedEvents.firstElement();
			receivedEvents.remove(receivedEvents.firstElement());
			return event;
		}

		public boolean available;
		public boolean canTLSEncrypt;
		public boolean tlsEncrypted;
		public boolean compressed;
		public boolean whitespacePingEnabled;
		public String bindID = "";
		public int resetCount;
		public Vector<Event> receivedEvents = new Vector<Event>();
	};

	public ClientSession createSession() {
		ClientSession session = ClientSession.create(new JID("me@foo.com"), server, idnConverter, crypto);
		session.onFinished.connect(new Slot1<com.isode.stroke.base.Error>() {
		@Override
		public void call(com.isode.stroke.base.Error e) {
			handleSessionFinished(e);
		}
		});
		session.onNeedCredentials.connect(new Slot() {
		@Override
		public void call() {
			handleSessionNeedCredentials();
		}
		});
		session.setAllowPLAINOverNonTLS(true);
		return session;
	}

	public void initializeSession(ClientSession session) {
		session.start();
		server.receiveStreamStart();
		server.sendStreamStart();
		server.sendStreamFeaturesWithPLAINAuthentication();
		session.sendCredentials(new SafeByteArray("mypass"));
		server.receiveAuthRequest("PLAIN");
		server.sendAuthSuccess();
		server.receiveStreamStart();
		server.sendStreamStart();
		server.sendStreamFeaturesWithBindAndStreamManagement();
		server.receiveBind();
		server.sendBindResult();
		server.receiveStreamManagementEnable();
		server.sendStreamManagementEnabled();
	}

	public void handleSessionFinished(com.isode.stroke.base.Error error) {
		sessionFinishedReceived = true;
		sessionFinishedError = error;
	}

	public void handleSessionNeedCredentials() {
		needCredentials = true;
	}

	@Before
	public void setUp() {
		crypto = new JavaCryptoProvider();
		idnConverter = new ICUConverter();
		server = new MockSessionStream();
		sessionFinishedReceived = false;
		needCredentials = false;
		blindCertificateTrustChecker = new BlindCertificateTrustChecker();
	}

	@Test
	public void testStart_Error() {
		ClientSession session = createSession();
		session.start();
		server.breakConnection();

		assertEquals(ClientSession.State.Finished, session.getState());
		assertTrue(sessionFinishedReceived);
		assertNotNull(sessionFinishedError);
	}

	@Test
	public void testStart_StreamError() {
		ClientSession session = createSession();
		session.start();
		server.sendStreamStart();
		server.sendStreamError();

		assertEquals(ClientSession.State.Finished, session.getState());
		assertTrue(sessionFinishedReceived);
		assertNotNull(sessionFinishedError);
	}

	@Test
	public void testStartTLS() {
		ClientSession session = createSession();
		session.setCertificateTrustChecker(blindCertificateTrustChecker);
		session.start();
		server.receiveStreamStart();
		server.sendStreamStart();
		server.sendStreamFeaturesWithStartTLS();
		server.receiveStartTLS();
		assertFalse(server.tlsEncrypted);
		server.sendTLSProceed();
		assertTrue(server.tlsEncrypted);
		server.onTLSEncrypted.emit();
		server.receiveStreamStart();
		server.sendStreamStart();

		session.finish();
	}

	@Test
	public void testStartTLS_ServerError() {
		ClientSession session = createSession();
		session.start();
		server.receiveStreamStart();
		server.sendStreamStart();
		server.sendStreamFeaturesWithStartTLS();
		server.receiveStartTLS();
		server.sendTLSFailure();

		assertFalse(server.tlsEncrypted);
		assertEquals(ClientSession.State.Finished, session.getState());
		assertTrue(sessionFinishedReceived);
		assertNotNull(sessionFinishedError);
	}

	@Test
	public void testStartTLS_ConnectError() {
		ClientSession session = createSession();
		session.start();
		server.receiveStreamStart();
		server.sendStreamStart();
		server.sendStreamFeaturesWithStartTLS();
		server.receiveStartTLS();
		server.sendTLSProceed();
		server.breakTLS();

		assertEquals(ClientSession.State.Finished, session.getState());
		assertTrue(sessionFinishedReceived);
		assertNotNull(sessionFinishedError);
	}

	@Test
	public void testStartTLS_InvalidIdentity() {
		ClientSession session = createSession();
		session.start();
		server.receiveStreamStart();
		server.sendStreamStart();
		server.sendStreamFeaturesWithStartTLS();
		server.receiveStartTLS();
		assertFalse(server.tlsEncrypted);
		server.sendTLSProceed();
		assertTrue(server.tlsEncrypted);
		server.onTLSEncrypted.emit();

		assertEquals(ClientSession.State.Finished, session.getState());
		assertTrue(sessionFinishedReceived);
		assertNotNull(sessionFinishedError);
		assertEquals(CertificateVerificationError.Type.InvalidServerIdentity, ((CertificateVerificationError)(sessionFinishedError)).getType());
	}

	@Test
	public void testStart_StreamFeaturesWithoutResourceBindingFails() {
		ClientSession session = createSession();
		session.start();
		server.receiveStreamStart();
		server.sendStreamStart();
		server.sendEmptyStreamFeatures();

		assertEquals(ClientSession.State.Finished, session.getState());
		assertTrue(sessionFinishedReceived);
		assertNotNull(sessionFinishedError);
	}

	@Test
	public void testAuthenticate() {
		ClientSession session = createSession();
		session.start();
		server.receiveStreamStart();
		server.sendStreamStart();
		server.sendStreamFeaturesWithPLAINAuthentication();
		assertTrue(needCredentials);
		assertEquals(ClientSession.State.WaitingForCredentials, session.getState());
		session.sendCredentials(new SafeByteArray("mypass"));
		server.receiveAuthRequest("PLAIN");
		server.sendAuthSuccess();
		server.receiveStreamStart();

		session.finish();
	}

	@Test
	public void testAuthenticate_Unauthorized() {
		ClientSession session = createSession();
		session.start();
		server.receiveStreamStart();
		server.sendStreamStart();
		server.sendStreamFeaturesWithPLAINAuthentication();
		assertTrue(needCredentials);
		assertEquals(ClientSession.State.WaitingForCredentials, session.getState());
		session.sendCredentials(new SafeByteArray("mypass"));
		server.receiveAuthRequest("PLAIN");
		server.sendAuthFailure();

		assertEquals(ClientSession.State.Finished, session.getState());
		assertTrue(sessionFinishedReceived);
		assertNotNull(sessionFinishedError);
	}

	@Test
	public void testAuthenticate_PLAINOverNonTLS() {
		ClientSession session = createSession();
		session.setAllowPLAINOverNonTLS(false);
		session.start();
		server.receiveStreamStart();
		server.sendStreamStart();
		server.sendStreamFeaturesWithPLAINAuthentication();

		assertEquals(ClientSession.State.Finished, session.getState());
		assertTrue(sessionFinishedReceived);
		assertNotNull(sessionFinishedError);
	}

	@Test
	public void testAuthenticate_RequireTLS() {
		ClientSession session = createSession();
		session.setUseTLS(ClientSession.UseTLS.RequireTLS);
		session.setAllowPLAINOverNonTLS(true);
		session.start();
		server.receiveStreamStart();
		server.sendStreamStart();
		server.sendStreamFeaturesWithMultipleAuthentication();

		assertEquals(ClientSession.State.Finished, session.getState());
		assertTrue(sessionFinishedReceived);
		assertNotNull(sessionFinishedError);
	}

	@Test
	public void testAuthenticate_NoValidAuthMechanisms() {
		ClientSession session = createSession();
		session.start();
		server.receiveStreamStart();
		server.sendStreamStart();
		server.sendStreamFeaturesWithUnknownAuthentication();

		assertEquals(ClientSession.State.Finished, session.getState());
		assertTrue(sessionFinishedReceived);
		assertNotNull(sessionFinishedError);
	}

	@Test
	public void testAuthenticate_EXTERNAL() {
		ClientSession session = createSession();
		session.start();
		server.receiveStreamStart();
		server.sendStreamStart();
		server.sendStreamFeaturesWithEXTERNALAuthentication();
		server.receiveAuthRequest("EXTERNAL");
		server.sendAuthSuccess();
		server.receiveStreamStart();

		session.finish();
	}

	@Test
	public void testUnexpectedChallenge() {
		ClientSession session = createSession();
		session.start();
		server.receiveStreamStart();
		server.sendStreamStart();
		server.sendStreamFeaturesWithEXTERNALAuthentication();
		server.receiveAuthRequest("EXTERNAL");
		server.sendChallenge();
		server.sendChallenge();

		assertEquals(ClientSession.State.Finished, session.getState());
		assertTrue(sessionFinishedReceived);
		assertNotNull(sessionFinishedError);
	}

	@Test
	public void testStreamManagement() {
		ClientSession session = createSession();
		session.start();
		server.receiveStreamStart();
		server.sendStreamStart();
		server.sendStreamFeaturesWithPLAINAuthentication();
		session.sendCredentials(new SafeByteArray("mypass"));
		server.receiveAuthRequest("PLAIN");
		server.sendAuthSuccess();
		server.receiveStreamStart();
		server.sendStreamStart();
		server.sendStreamFeaturesWithBindAndStreamManagement();
		server.receiveBind();
		server.sendBindResult();
		server.receiveStreamManagementEnable();
		server.sendStreamManagementEnabled();

		assertTrue(session.getStreamManagementEnabled());
		// TODO: Test if the requesters & responders do their work
		assertEquals(ClientSession.State.Initialized, session.getState());

		session.finish();
	}

	@Test
	public void testStreamManagement_Failed() {
		ClientSession session = createSession();
		session.start();
		server.receiveStreamStart();
		server.sendStreamStart();
		server.sendStreamFeaturesWithPLAINAuthentication();
		session.sendCredentials(new SafeByteArray("mypass"));
		server.receiveAuthRequest("PLAIN");
		server.sendAuthSuccess();
		server.receiveStreamStart();
		server.sendStreamStart();
		server.sendStreamFeaturesWithBindAndStreamManagement();
		server.receiveBind();
		server.sendBindResult();
		server.receiveStreamManagementEnable();
		server.sendStreamManagementFailed();

		assertFalse(session.getStreamManagementEnabled());
		assertEquals(ClientSession.State.Initialized, session.getState());

		session.finish();
	}

	@Test
	public void testFinishAcksStanzas() {
		ClientSession session = createSession();
		initializeSession(session);
		server.sendMessage();
		server.sendMessage();
		server.sendMessage();

		session.finish();

		server.receiveAck(3);
	}

	/*void testAuthenticate() {
		boost::shared_ptr<MockSession> session(createSession("me@foo.com/Bar"));
		session->onNeedCredentials.connect(boost::bind(&ClientSessionTest::setNeedCredentials, this));
		getMockServer()->expectStreamStart();
		getMockServer()->sendStreamStart();
		getMockServer()->sendStreamFeaturesWithAuthentication();
		session->startSession();
		processEvents();
		CPPUNIT_ASSERT_EQUAL(ClientSession::WaitingForCredentials, session->getState());
		CPPUNIT_ASSERT(needCredentials_);

		getMockServer()->expectAuth("me", "mypass");
		getMockServer()->sendAuthSuccess();
		getMockServer()->expectStreamStart();
		getMockServer()->sendStreamStart();
		session->sendCredentials("mypass");
		CPPUNIT_ASSERT_EQUAL(ClientSession::Authenticating, session->getState());
		processEvents();
		CPPUNIT_ASSERT_EQUAL(ClientSession::Negotiating, session->getState());
	}

	void testAuthenticate_Unauthorized() {
		boost::shared_ptr<MockSession> session(createSession("me@foo.com/Bar"));
		getMockServer()->expectStreamStart();
		getMockServer()->sendStreamStart();
		getMockServer()->sendStreamFeaturesWithAuthentication();
		session->startSession();
		processEvents();

		getMockServer()->expectAuth("me", "mypass");
		getMockServer()->sendAuthFailure();
		session->sendCredentials("mypass");
		processEvents();

		CPPUNIT_ASSERT_EQUAL(ClientSession::Error, session->getState());
		CPPUNIT_ASSERT_EQUAL(ClientSession::AuthenticationFailedError, *session->getError());
	}

	void testAuthenticate_NoValidAuthMechanisms() {
		boost::shared_ptr<MockSession> session(createSession("me@foo.com/Bar"));
		getMockServer()->expectStreamStart();
		getMockServer()->sendStreamStart();
		getMockServer()->sendStreamFeaturesWithUnsupportedAuthentication();
		session->startSession();
		processEvents();

		CPPUNIT_ASSERT_EQUAL(ClientSession::Error, session->getState());
		CPPUNIT_ASSERT_EQUAL(ClientSession::NoSupportedAuthMechanismsError, *session->getError());
	}

	void testResourceBind() {
		boost::shared_ptr<MockSession> session(createSession("me@foo.com/Bar"));
		getMockServer()->expectStreamStart();
		getMockServer()->sendStreamStart();
		getMockServer()->sendStreamFeaturesWithResourceBind();
		getMockServer()->expectResourceBind("Bar", "session-bind");
		// FIXME: Check CPPUNIT_ASSERT_EQUAL(ClientSession::BindingResource, session->getState());
		getMockServer()->sendResourceBindResponse("me@foo.com/Bar", "session-bind");
		session->startSession();

		processEvents();

		CPPUNIT_ASSERT_EQUAL(ClientSession::SessionStarted, session->getState());
		CPPUNIT_ASSERT_EQUAL(JID("me@foo.com/Bar"), session->getLocalJID());
	}

	void testResourceBind_ChangeResource() {
		boost::shared_ptr<MockSession> session(createSession("me@foo.com/Bar"));
		getMockServer()->expectStreamStart();
		getMockServer()->sendStreamStart();
		getMockServer()->sendStreamFeaturesWithResourceBind();
		getMockServer()->expectResourceBind("Bar", "session-bind");
		getMockServer()->sendResourceBindResponse("me@foo.com/Bar123", "session-bind");
		session->startSession();
		processEvents();

		CPPUNIT_ASSERT_EQUAL(ClientSession::SessionStarted, session->getState());
		CPPUNIT_ASSERT_EQUAL(JID("me@foo.com/Bar123"), session->getLocalJID());
	}

	void testResourceBind_EmptyResource() {
		boost::shared_ptr<MockSession> session(createSession("me@foo.com"));
		getMockServer()->expectStreamStart();
		getMockServer()->sendStreamStart();
		getMockServer()->sendStreamFeaturesWithResourceBind();
		getMockServer()->expectResourceBind("", "session-bind");
		getMockServer()->sendResourceBindResponse("me@foo.com/NewResource", "session-bind");
		session->startSession();
		processEvents();

		CPPUNIT_ASSERT_EQUAL(ClientSession::SessionStarted, session->getState());
		CPPUNIT_ASSERT_EQUAL(JID("me@foo.com/NewResource"), session->getLocalJID());
	}

	void testResourceBind_Error() {
		boost::shared_ptr<MockSession> session(createSession("me@foo.com"));
		getMockServer()->expectStreamStart();
		getMockServer()->sendStreamStart();
		getMockServer()->sendStreamFeaturesWithResourceBind();
		getMockServer()->expectResourceBind("", "session-bind");
		getMockServer()->sendError("session-bind");
		session->startSession();
		processEvents();

		CPPUNIT_ASSERT_EQUAL(ClientSession::Error, session->getState());
		CPPUNIT_ASSERT_EQUAL(ClientSession::ResourceBindError, *session->getError());
	}

	void testSessionStart() {
		boost::shared_ptr<MockSession> session(createSession("me@foo.com/Bar"));
		session->onSessionStarted.connect(boost::bind(&ClientSessionTest::setSessionStarted, this));
		getMockServer()->expectStreamStart();
		getMockServer()->sendStreamStart();
		getMockServer()->sendStreamFeaturesWithSession();
		getMockServer()->expectSessionStart("session-start");
		// FIXME: Check CPPUNIT_ASSERT_EQUAL(ClientSession::StartingSession, session->getState());
		getMockServer()->sendSessionStartResponse("session-start");
		session->startSession();
		processEvents();

		CPPUNIT_ASSERT_EQUAL(ClientSession::SessionStarted, session->getState());
		CPPUNIT_ASSERT(sessionStarted_);
	}

	void testSessionStart_Error() {
		boost::shared_ptr<MockSession> session(createSession("me@foo.com/Bar"));
		getMockServer()->expectStreamStart();
		getMockServer()->sendStreamStart();
		getMockServer()->sendStreamFeaturesWithSession();
		getMockServer()->expectSessionStart("session-start");
		getMockServer()->sendError("session-start");
		session->startSession();
		processEvents();

		CPPUNIT_ASSERT_EQUAL(ClientSession::Error, session->getState());
		CPPUNIT_ASSERT_EQUAL(ClientSession::SessionStartError, *session->getError());
	}

	void testSessionStart_AfterResourceBind() {
		boost::shared_ptr<MockSession> session(createSession("me@foo.com/Bar"));
		session->onSessionStarted.connect(boost::bind(&ClientSessionTest::setSessionStarted, this));
		getMockServer()->expectStreamStart();
		getMockServer()->sendStreamStart();
		getMockServer()->sendStreamFeaturesWithResourceBindAndSession();
		getMockServer()->expectResourceBind("Bar", "session-bind");
		getMockServer()->sendResourceBindResponse("me@foo.com/Bar", "session-bind");
		getMockServer()->expectSessionStart("session-start");
		getMockServer()->sendSessionStartResponse("session-start");
		session->startSession();
		processEvents();

		CPPUNIT_ASSERT_EQUAL(ClientSession::SessionStarted, session->getState());
		CPPUNIT_ASSERT(sessionStarted_);
	}

	void testWhitespacePing() {
		boost::shared_ptr<MockSession> session(createSession("me@foo.com/Bar"));
		getMockServer()->expectStreamStart();
		getMockServer()->sendStreamStart();
		getMockServer()->sendStreamFeatures();
		session->startSession();
		processEvents();
		CPPUNIT_ASSERT(session->getWhitespacePingLayer());
	}

	void testReceiveElementAfterSessionStarted() {
		boost::shared_ptr<MockSession> session(createSession("me@foo.com/Bar"));
		getMockServer()->expectStreamStart();
		getMockServer()->sendStreamStart();
		getMockServer()->sendStreamFeatures();
		session->startSession();
		processEvents();

		getMockServer()->expectMessage();
		session->sendElement(boost::make_shared<Message>()));
	}

	void testSendElement() {
		boost::shared_ptr<MockSession> session(createSession("me@foo.com/Bar"));
		session->onElementReceived.connect(boost::bind(&ClientSessionTest::addReceivedElement, this, _1));
		getMockServer()->expectStreamStart();
		getMockServer()->sendStreamStart();
		getMockServer()->sendStreamFeatures();
		getMockServer()->sendMessage();
		session->startSession();
		processEvents();

		CPPUNIT_ASSERT_EQUAL(1, static_cast<int>(receivedElements_.size()));
		CPPUNIT_ASSERT(boost::dynamic_pointer_cast<Message>(receivedElements_[0]));
	}*/
}