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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Before;
import com.isode.stroke.component.ComponentSession;
import com.isode.stroke.elements.ComponentHandshake;
import com.isode.stroke.elements.AuthFailure;
import com.isode.stroke.elements.ProtocolHeader;
import com.isode.stroke.elements.Element;
import com.isode.stroke.base.ByteArray;
import com.isode.stroke.tls.Certificate;
import com.isode.stroke.tls.CertificateVerificationError;
import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.crypto.JavaCryptoProvider;
import com.isode.stroke.session.SessionStream;
import com.isode.stroke.jid.JID;
import com.isode.stroke.signals.Slot1;
import java.util.Vector;

public class ComponentSessionTest {

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
			this.whitespacePingEnabled = false;
			this.resetCount = 0;
		}

		public void close() {
			onClosed.emit((SessionStream.Error)null);
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

		public void writeData(final String str) {

		}

		public boolean supportsTLSEncryption() {
			return false;
		}

		public void addTLSEncryption() {
			assert(false);
		}

		public boolean isTLSEncrypted() {
			return false;
		}

		public ByteArray getTLSFinishMessage() {
			return new ByteArray();
		}

		public Certificate getPeerCertificate() {
			return (Certificate)null;
		}

		public Vector<Certificate> getPeerCertificateChain() {
			 return (Vector<Certificate>)null;
		}

		public CertificateVerificationError getPeerCertificateVerificationError() {
			return (CertificateVerificationError)null;
		}

		public boolean supportsZLibCompression() {
			return true;
		}

		public void addZLibCompression() {
			assert(false);
		}

		public void setWhitespacePingEnabled(boolean enabled) {
			whitespacePingEnabled = enabled;
		}

		public void resetXMPPParser() {
			resetCount++;
		}

		public void breakConnection() {
			onClosed.emit(new SessionStream.Error(SessionStream.Error.Type.ConnectionReadError));
		}

		public void sendStreamStart() {
			ProtocolHeader header = new ProtocolHeader();
			header.setFrom("service.foo.com");
			onStreamStartReceived.emit(header);
		}

		public void sendHandshakeResponse() {
			onElementReceived.emit(new ComponentHandshake());
		}

		public void sendHandshakeError() {
			// FIXME: This isn't the correct element
			onElementReceived.emit(new AuthFailure());
		}

		public void receiveStreamStart() {
			Event event = popEvent();
			assertNotNull(event.header);
		}

		public void receiveHandshake() {
			Event event = popEvent();
			assertNotNull(event.element);
			ComponentHandshake handshake = (ComponentHandshake)(event.element);
			assertNotNull(handshake);
			assertEquals("4c4f8a41141722c8bbfbdd92d827f7b2fc0a542b", handshake.getData());
		}

		public Event popEvent() {
			assertFalse(receivedEvents.isEmpty());
			Event event = receivedEvents.firstElement();
			receivedEvents.remove(receivedEvents.firstElement());
			return event;
		}

		public boolean available;
		public boolean whitespacePingEnabled;
		public String bindID = "";
		public int resetCount;
		public Vector<Event> receivedEvents = new Vector<Event>();
	};

	private ComponentSession createSession() {
		ComponentSession session = ComponentSession.create(new JID("service.foo.com"), "servicesecret", server, crypto);
		session.onFinished.connect(new Slot1<com.isode.stroke.base.Error>() {
			@Override
			public void call(com.isode.stroke.base.Error e1) {
				handleSessionFinished(e1);
			}
		});
		return session;
	}

	private void handleSessionFinished(com.isode.stroke.base.Error error) {
		sessionFinishedReceived = true;
		sessionFinishedError = error;
	}

	private MockSessionStream server;
	private boolean sessionFinishedReceived;
	private com.isode.stroke.base.Error sessionFinishedError;
	private CryptoProvider crypto;

	@Before
	public void setUp() {
		server = new MockSessionStream();
		sessionFinishedReceived = false;
		crypto = new JavaCryptoProvider();
	}

	@Test
	public void testStart() {
		ComponentSession session = createSession();
		session.start();
		server.receiveStreamStart();
		server.sendStreamStart();
		server.receiveHandshake();
		server.sendHandshakeResponse();

		assertNotNull(server.whitespacePingEnabled);

		session.finish();
		assertFalse(server.whitespacePingEnabled);
	}

	@Test
	public void testStart_Error() {
		ComponentSession session = createSession();
		session.start();
		server.breakConnection();

		assertEquals(ComponentSession.State.Finished, session.getState());
		assertTrue(sessionFinishedReceived);
		assertNotNull(sessionFinishedError);
	}

	@Test
	public void testStart_Unauthorized() {
		ComponentSession session = createSession();
		session.start();
		server.receiveStreamStart();
		server.sendStreamStart();
		server.receiveHandshake();
		server.sendHandshakeError();

		assertEquals(ComponentSession.State.Finished, session.getState());
		assertTrue(sessionFinishedReceived);
		assertNotNull(sessionFinishedError);
	}
}
