/*
 * Copyright (c) 2013-2015 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.filetransfer;

import com.isode.stroke.elements.JingleS5BTransportPayload;
import com.isode.stroke.signals.Signal2;
import com.isode.stroke.signals.Signal3;
import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.jid.JID;
import java.util.Vector;
import java.util.logging.Logger;
import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.queries.GenericRequest;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.signals.Slot2;
import com.isode.stroke.base.IDGenerator;
import com.isode.stroke.base.SafeByteArray;
import com.isode.stroke.network.ConnectionFactory;
import com.isode.stroke.network.TimerFactory;
import com.isode.stroke.elements.S5BProxyRequest;
import com.isode.stroke.elements.IQ;
import com.isode.stroke.stringcodecs.Hexify;

public class DefaultFileTransferTransporter extends FileTransferTransporter {

	private JID initiator;
	private JID responder;
	private Role role;
	private SOCKS5BytestreamRegistry s5bRegistry;
	private SOCKS5BytestreamServerManager s5bServerManager;
	private SOCKS5BytestreamProxiesManager s5bProxy;
	private CryptoProvider crypto;
	private IQRouter router;
	private LocalJingleTransportCandidateGenerator localCandidateGenerator;
	private RemoteJingleTransportCandidateSelector remoteCandidateSelector;
	private String s5bSessionID;
	private SOCKS5BytestreamClientSession remoteS5BClientSession;
	private Logger logger_ = Logger.getLogger(this.getClass().getName());

	public enum Role {
		Initiator,
		Responder
	};

	public DefaultFileTransferTransporter(
		final JID initiator, 
		final JID responder,
		Role role,
		SOCKS5BytestreamRegistry s5bRegistry, 
		SOCKS5BytestreamServerManager s5bServerManager,
		SOCKS5BytestreamProxiesManager s5bProxy, 
		IDGenerator idGenerator, 
		ConnectionFactory connectionFactory, 
		TimerFactory timerFactory, 
		CryptoProvider crypto,
		IQRouter router,
		final FileTransferOptions options) {
		this.initiator = initiator;
		this.responder = responder;
		this.role = role;
		this.s5bRegistry = s5bRegistry;
		this.s5bServerManager = s5bServerManager;
		this.s5bProxy = s5bProxy;
		this.crypto = crypto;
		this.router = router;
		localCandidateGenerator = new LocalJingleTransportCandidateGenerator(
				s5bServerManager,
				s5bProxy,
				(role == Role.Initiator ? initiator : responder),
				idGenerator,
				options);
		localCandidateGenerator.onLocalTransportCandidatesGenerated.connect(new Slot1<Vector<JingleS5BTransportPayload.Candidate>>() {
			@Override
			public void call(Vector<JingleS5BTransportPayload.Candidate> e) {
				handleLocalCandidatesGenerated(e);
			}
		});

		remoteCandidateSelector = new RemoteJingleTransportCandidateSelector(
				connectionFactory,
				timerFactory,
				options);
		remoteCandidateSelector.onCandidateSelectFinished.connect(new Slot2<JingleS5BTransportPayload.Candidate, SOCKS5BytestreamClientSession>() {
			@Override
			public void call(JingleS5BTransportPayload.Candidate c, SOCKS5BytestreamClientSession s) {
				handleRemoteCandidateSelectFinished(c, s);
			}
		});
	}

	public void initialize() {
		s5bSessionID = s5bRegistry.generateSessionID();
	}

	public void initialize(final String s5bSessionID) {
		this.s5bSessionID = s5bSessionID;
	}

	public void startGeneratingLocalCandidates() {
		localCandidateGenerator.start();
	}

	public void stopGeneratingLocalCandidates() {
		localCandidateGenerator.stop();
	}

	public void addRemoteCandidates(
					final Vector<JingleS5BTransportPayload.Candidate> candidates, final String dstAddr) {
		remoteCandidateSelector.setSOCKS5DstAddr(dstAddr.isEmpty() ? getRemoteCandidateSOCKS5DstAddr() : dstAddr);
		remoteCandidateSelector.addCandidates(candidates);
	}

	public void startTryingRemoteCandidates() {
		remoteCandidateSelector.startSelectingCandidate();
	}

	public void stopTryingRemoteCandidates() {
		remoteCandidateSelector.stopSelectingCandidate();
	}

	public void startActivatingProxy(final JID proxyServiceJID) {
		// activate proxy
		logger_.fine("Start activating proxy " + proxyServiceJID.toString() + " with sid = " + s5bSessionID + ".\n");
		S5BProxyRequest proxyRequest = new S5BProxyRequest();
		proxyRequest.setSID(s5bSessionID);
		proxyRequest.setActivate(role == Role.Initiator ? responder : initiator);

		GenericRequest<S5BProxyRequest> request = new GenericRequest<S5BProxyRequest>(IQ.Type.Set, proxyServiceJID, proxyRequest, router);
		request.onResponse.connect(new Slot2<S5BProxyRequest, ErrorPayload>() {
			@Override
			public void call(S5BProxyRequest s, ErrorPayload e) {
				handleActivateProxySessionResult(s5bSessionID, e);
			}
		});
		request.send();
	}

	public void stopActivatingProxy() {
		// TODO
		assert(false);
	}

	public TransportSession createIBBSendSession(
					final String sessionID, int blockSize, ReadBytestream stream) {
		if (s5bServerManager.getServer() != null) {
			closeLocalSession();
		}
		closeRemoteSession();
		IBBSendSession ibbSession = new IBBSendSession(
				sessionID, initiator, responder, stream, router);
		ibbSession.setBlockSize(blockSize);
		return new IBBSendTransportSession(ibbSession);
	}

	public TransportSession createIBBReceiveSession(
					final String sessionID, int size, WriteBytestream stream) {
		if (s5bServerManager.getServer() != null) {
			closeLocalSession();
		}
		closeRemoteSession();
		IBBReceiveSession ibbSession = new IBBReceiveSession(
				sessionID, initiator, responder, size, stream, router);
		return new IBBReceiveTransportSession(ibbSession);
	}

	public TransportSession createRemoteCandidateSession(
					ReadBytestream stream, final JingleS5BTransportPayload.Candidate candidate) {
		closeLocalSession();
		return new S5BTransportSession<SOCKS5BytestreamClientSession>(
			remoteS5BClientSession, stream);
	}

	public TransportSession createRemoteCandidateSession(
					WriteBytestream stream, final JingleS5BTransportPayload.Candidate candidate) {
		closeLocalSession();
		return new S5BTransportSession<SOCKS5BytestreamClientSession>(
			remoteS5BClientSession, stream);
	}

	public TransportSession createLocalCandidateSession(
					ReadBytestream stream, final JingleS5BTransportPayload.Candidate candidate) {
		closeRemoteSession();
		TransportSession transportSession = null;
		if (JingleS5BTransportPayload.Candidate.Type.ProxyType.equals(candidate.type)) {
			SOCKS5BytestreamClientSession proxySession = s5bProxy.getProxySessionAndCloseOthers(candidate.jid, getLocalCandidateSOCKS5DstAddr());
			assert(proxySession != null);
			transportSession = new S5BTransportSession<SOCKS5BytestreamClientSession>(proxySession, stream);
		}

		if (transportSession == null) {
			SOCKS5BytestreamServerSession serverSession = getServerSession();
			if (serverSession != null) {
				transportSession = new S5BTransportSession<SOCKS5BytestreamServerSession>(serverSession, stream);
			}
		}

		if (transportSession == null) {
			transportSession = new FailingTransportSession();
		}
		return transportSession;
	}

	public TransportSession createLocalCandidateSession(
					WriteBytestream stream, final JingleS5BTransportPayload.Candidate candidate) {
		closeRemoteSession();
		TransportSession transportSession = null;
		if (JingleS5BTransportPayload.Candidate.Type.ProxyType.equals(candidate.type)) {
			SOCKS5BytestreamClientSession proxySession = s5bProxy.getProxySessionAndCloseOthers(candidate.jid, getLocalCandidateSOCKS5DstAddr());
			assert(proxySession != null);
			transportSession = new S5BTransportSession<SOCKS5BytestreamClientSession>(proxySession, stream);
		}

		if (transportSession == null) {
			SOCKS5BytestreamServerSession serverSession = getServerSession();
			if (serverSession != null) {
				transportSession = new S5BTransportSession<SOCKS5BytestreamServerSession>(serverSession, stream);
			}
		}

		if (transportSession == null) {
			transportSession = new FailingTransportSession();
		}
		return transportSession;
	}

	private void handleLocalCandidatesGenerated(final Vector<JingleS5BTransportPayload.Candidate> candidates) {
		s5bRegistry.setHasBytestream(getSOCKS5DstAddr(), true);
		s5bProxy.connectToProxies(getSOCKS5DstAddr());
		onLocalCandidatesGenerated.emit(s5bSessionID, candidates, getSOCKS5DstAddr());
	}

	private void handleRemoteCandidateSelectFinished(
			final JingleS5BTransportPayload.Candidate candidate, 
			SOCKS5BytestreamClientSession session) {
		remoteS5BClientSession = session;
		onRemoteCandidateSelectFinished.emit(s5bSessionID, candidate);
	}

	private void handleActivateProxySessionResult(final String sessionID, ErrorPayload error) {
		onProxyActivated.emit(sessionID, error);
	}

	private void closeLocalSession() {
		s5bRegistry.setHasBytestream(getSOCKS5DstAddr(), false);
		if (s5bServerManager.getServer() != null) {
			Vector<SOCKS5BytestreamServerSession> serverSessions = s5bServerManager.getServer().getSessions(getSOCKS5DstAddr());
			for(SOCKS5BytestreamServerSession session : serverSessions) {
				session.stop();
			}
		}
	}
	private void closeRemoteSession() {
		if (remoteS5BClientSession != null) {
			remoteS5BClientSession.stop();
			remoteS5BClientSession = null;
		}
	}

	private SOCKS5BytestreamServerSession getServerSession() {
		s5bRegistry.setHasBytestream(getSOCKS5DstAddr(), false);
		Vector<SOCKS5BytestreamServerSession> serverSessions = s5bServerManager.getServer().getSessions(getSOCKS5DstAddr());
		while (serverSessions.size() > 1) {
			SOCKS5BytestreamServerSession session = serverSessions.lastElement();
			serverSessions.remove(serverSessions.lastElement());
			session.stop();
		}
		return !serverSessions.isEmpty() ? serverSessions.get(0) : null;
	}

	private String getSOCKS5DstAddr() {
		String result = "";
		if (Role.Initiator.equals(role)) {
			result = getInitiatorCandidateSOCKS5DstAddr();
			logger_.fine("Initiator S5B DST.ADDR = " + s5bSessionID + " + " + initiator.toString() + " + " + responder.toString() + " : " + result + "\n");
		}
		else {
			result = getResponderCandidateSOCKS5DstAddr();
			logger_.fine("Responder S5B DST.ADDR = " + s5bSessionID + " + " + responder.toString() + " + " + initiator.toString() + " : " + result + "\n");
		}
		return result;
	}

	private String getInitiatorCandidateSOCKS5DstAddr() {
		return Hexify.hexify(crypto.getSHA1Hash(new SafeByteArray(s5bSessionID + initiator.toString() + responder.toString())));
	}

	private String getResponderCandidateSOCKS5DstAddr() {
		return Hexify.hexify(crypto.getSHA1Hash(new SafeByteArray(s5bSessionID + responder.toString() + initiator.toString())));
	}

	private String getRemoteCandidateSOCKS5DstAddr() {
		if (Role.Initiator.equals(role)) {
			return getResponderCandidateSOCKS5DstAddr();
		}
		else {
			return getInitiatorCandidateSOCKS5DstAddr();
		}
	}

	private String getLocalCandidateSOCKS5DstAddr() {
		if (Role.Responder.equals(role)) {
			return getResponderCandidateSOCKS5DstAddr();
		}
		else {
			return getInitiatorCandidateSOCKS5DstAddr();
		}
	}
}