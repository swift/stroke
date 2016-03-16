/*
 * Copyright (c) 2015-2016 Isode Limited.
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
import com.isode.stroke.elements.S5BProxyRequest;

class DummyFileTransferTransporter extends FileTransferTransporter {

	public enum Role {
		Initiator,
		Responder
	};

	public DummyFileTransferTransporter(
			final JID initiator,
			final JID responder,
			Role role,
			SOCKS5BytestreamRegistry s5bRegistry,
			SOCKS5BytestreamServerManager s5bServerManager,
			SOCKS5BytestreamProxiesManager s5bProxy,
			IDGenerator idGenerator,
			ConnectionFactory connectionFactory,
			TimerFactory timer,
			CryptoProvider cryptoProvider,
			IQRouter iqRouter,
			final FileTransferOptions ftOptions) {
		initiator_ = initiator;
		responder_ = responder;
		role_ = role;
		s5bRegistry_ = s5bRegistry;
		crypto_ = cryptoProvider;
		iqRouter_ = iqRouter;
		ftOptions_ = new FileTransferOptions(ftOptions);
	}

	public void initialize() {
		s5bSessionID_ = s5bRegistry_.generateSessionID();
	}

	public void startGeneratingLocalCandidates() {
		Vector<JingleS5BTransportPayload.Candidate> candidates = new Vector<JingleS5BTransportPayload.Candidate>();
		if (ftOptions_.isDirectAllowed()) {
            JingleS5BTransportPayload.Candidate candidate = new JingleS5BTransportPayload.Candidate();
            candidate.cid = "123";
            candidate.priority = 1235;
            candidates.add(candidate);
        }
		onLocalCandidatesGenerated.emit(s5bSessionID_, candidates, getSOCKS5DstAddr());
	}

	public void stopGeneratingLocalCandidates() {
	}

	public void addRemoteCandidates(final Vector<JingleS5BTransportPayload.Candidate> candidates, final String d) {
	}

	public void startTryingRemoteCandidates() {
		onRemoteCandidateSelectFinished.emit(s5bSessionID_, null);
	}

	public void stopTryingRemoteCandidates() {
	}

	public void startActivatingProxy(final JID proxy) {
	}

	public void stopActivatingProxy() {
	}

	public TransportSession createIBBSendSession(final String sessionID, int blockSize, ReadBytestream stream) {
		IBBSendSession ibbSession =new IBBSendSession(
				sessionID, initiator_, responder_, stream, iqRouter_);
		ibbSession.setBlockSize(blockSize);
		return new IBBSendTransportSession(ibbSession);
	}

	public TransportSession createIBBReceiveSession(final String sessionID, int size, WriteBytestream stream) {
		IBBReceiveSession ibbSession = new IBBReceiveSession(
				sessionID, initiator_, responder_, size, stream, iqRouter_);
		return new IBBReceiveTransportSession(ibbSession);
	}

	public TransportSession createRemoteCandidateSession(
			ReadBytestream stream, final JingleS5BTransportPayload.Candidate candidate) {
		return null;
	}

	public TransportSession createRemoteCandidateSession(
			WriteBytestream stream, final JingleS5BTransportPayload.Candidate candidate) {
		return null;
	}

	public TransportSession createLocalCandidateSession(
			ReadBytestream stream, final JingleS5BTransportPayload.Candidate candidate) {
		return null;
	}

	public TransportSession createLocalCandidateSession(
			WriteBytestream stream, final JingleS5BTransportPayload.Candidate candidate) {
		return null;
	}

	private String getSOCKS5DstAddr() {
		String result = "";
		if (Role.Initiator.equals(role_)) {
			result = getInitiatorCandidateSOCKS5DstAddr();
		}
		else {
			result = getResponderCandidateSOCKS5DstAddr();
		}
		return result;
	}

	private String getInitiatorCandidateSOCKS5DstAddr() {
		return Hexify.hexify(crypto_.getSHA1Hash(new SafeByteArray(s5bSessionID_ + initiator_.toString() + responder_.toString())));
	}

	private String getResponderCandidateSOCKS5DstAddr() {
		return Hexify.hexify(crypto_.getSHA1Hash(new SafeByteArray(s5bSessionID_ + responder_.toString() + initiator_.toString())));
	}

	private JID initiator_;
	private JID responder_;
	private Role role_;
	private SOCKS5BytestreamRegistry s5bRegistry_;
	private CryptoProvider crypto_;
	private String s5bSessionID_;
	private IQRouter iqRouter_;
	private final FileTransferOptions ftOptions_;
};

public class DummyFileTransferTransporterFactory implements FileTransferTransporterFactory {

	public DummyFileTransferTransporterFactory(
		SOCKS5BytestreamRegistry s5bRegistry,
		SOCKS5BytestreamServerManager s5bServerManager,
		SOCKS5BytestreamProxiesManager s5bProxy,
		IDGenerator idGenerator,
		ConnectionFactory connectionFactory,
		TimerFactory timerFactory,
		CryptoProvider cryptoProvider,
		IQRouter iqRouter) {
		s5bRegistry_ = s5bRegistry;
		s5bServerManager_ = s5bServerManager;
		s5bProxy_ = s5bProxy;
		idGenerator_ = idGenerator;
		connectionFactory_ = connectionFactory;
		timerFactory_ = timerFactory;
		cryptoProvider_ = cryptoProvider;
		iqRouter_ = iqRouter;
	}

	public FileTransferTransporter createInitiatorTransporter(final JID initiator, final JID responder, final FileTransferOptions options) {
			DummyFileTransferTransporter transporter = new DummyFileTransferTransporter(
				initiator,
				responder,
				DummyFileTransferTransporter.Role.Initiator,
				s5bRegistry_,
				s5bServerManager_,
				s5bProxy_,
				idGenerator_,
				connectionFactory_,
				timerFactory_,
				cryptoProvider_,
				iqRouter_,
				options);
			transporter.initialize();
			return transporter;
	}

	public FileTransferTransporter createResponderTransporter(final JID initiator, final JID responder, final String s5bSessionID, final FileTransferOptions options) {
		return null;
	}

	private SOCKS5BytestreamRegistry s5bRegistry_;
	private SOCKS5BytestreamServerManager s5bServerManager_;
	private SOCKS5BytestreamProxiesManager s5bProxy_;
	private IDGenerator idGenerator_;
	private ConnectionFactory connectionFactory_;
	private TimerFactory timerFactory_;
	private CryptoProvider cryptoProvider_;
	private IQRouter iqRouter_;
};