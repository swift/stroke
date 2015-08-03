/*
 * Copyright (c) 2011 Tobias Markmann
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */
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

import com.isode.stroke.jid.JID;
import com.isode.stroke.jingle.JingleSession;
import com.isode.stroke.network.TimerFactory;
import com.isode.stroke.network.Timer;
import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.base.IDGenerator;
import com.isode.stroke.base. ByteArray;
import com.isode.stroke.elements.JingleFileTransferFileInfo;
import com.isode.stroke.elements.JingleDescription;
import com.isode.stroke.elements.JingleTransportPayload;
import com.isode.stroke.elements.JingleIBBTransportPayload;
import com.isode.stroke.elements.JingleS5BTransportPayload;
import com.isode.stroke.elements.JinglePayload;
import com.isode.stroke.elements.JingleContentPayload;
import com.isode.stroke.elements.JingleFileTransferDescription;
import com.isode.stroke.elements.HashElement;
import com.isode.stroke.elements.JingleFileTransferHash;
import com.isode.stroke.jingle.JingleContentID;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.signals.Slot;
import java.util.logging.Logger;
import java.util.Vector;

public class OutgoingJingleFileTransfer extends JingleFileTransfer implements OutgoingFileTransfer {

	private long fileSizeInBytes = 0; //FileTransferVariables
	private String filename = ""; //FileTransferVariables

	/**
	* FileTransferMethod.
	*/
	@Override
	public String getFileName() {
		return filename;
	}

	/**
	* FileTransferMethod.
	*/
	@Override
	public long getFileSizeInBytes() {
		return fileSizeInBytes;
	}

	/**
	* FileTransferMethod.
	*/
	@Override
	public void setFileInfo(final String name, long size) {
		this.filename = name;
		this.fileSizeInBytes = size;
	}

	public static final int DEFAULT_BLOCK_SIZE = 4096;
	private IDGenerator idGenerator;
	private ReadBytestream stream;
	private JingleFileTransferFileInfo fileInfo;
	private FileTransferOptions options;
	private JingleContentID contentID;
	private IncrementalBytestreamHashCalculator hashCalculator;
	private State state;
	private boolean candidateAcknowledged;

	private Timer waitForRemoteTermination;

	private SignalConnection processedBytesConnection;
	private SignalConnection transferFinishedConnection;
	private Logger logger_ = Logger.getLogger(this.getClass().getName());

	public OutgoingJingleFileTransfer(
		final JID toJID,
		JingleSession session,
		ReadBytestream stream,
		FileTransferTransporterFactory transporterFactory,
		TimerFactory timerFactory,
		IDGenerator idGenerator,
		final JingleFileTransferFileInfo fileInfo,
		final FileTransferOptions options,
		CryptoProvider crypto) {
		super(session, toJID, transporterFactory);
		this.idGenerator = idGenerator;
		this.stream = stream;
		this.fileInfo = fileInfo;
		this.session = session;
		this.contentID = new JingleContentID(idGenerator.generateID(), JingleContentPayload.Creator.InitiatorCreator);
		this.state = State.Initial;
		this.candidateAcknowledged = false;
		setFileInfo(fileInfo.getName(), fileInfo.getSize());

		// calculate both, MD5 and SHA-1 since we don't know which one the other side supports
		hashCalculator = new IncrementalBytestreamHashCalculator(true, true, crypto);
		stream.onRead.connect(new Slot1<ByteArray>() {
			@Override
			public void call(ByteArray b) {
				hashCalculator.feedData(b);
			}
		});
		waitForRemoteTermination = timerFactory.createTimer(5000);
		waitForRemoteTermination.onTick.connect(new Slot() {
			@Override
			public void call() {
				handleWaitForRemoteTerminationTimeout();
			}
		});
	}

	/**
	* OutgoingFileTransferMethod.
	*/
	@Override
	public void start() {
		logger_.fine("\n");
		if (!State.Initial.equals(state)) {
			logger_.warning("Incorrect state\n");
			return;
		}

		setTransporter(transporterFactory.createInitiatorTransporter(getInitiator(), getResponder(), options));
		setState(State.GeneratingInitialLocalCandidates);
		transporter.startGeneratingLocalCandidates();
	}

	/**
	* JingleFileTransferMethod.
	*/
	@Override
	public void cancel() {
		terminate(JinglePayload.Reason.Type.Cancel);
	}

	private enum State {
		Initial,
		GeneratingInitialLocalCandidates,
		WaitingForAccept,
		TryingCandidates,
		WaitingForPeerProxyActivate,
		WaitingForLocalProxyActivate,
		WaitingForCandidateAcknowledge,
		FallbackRequested,
		Transferring,
		WaitForTermination,
		Finished
	};

	public void handleSessionAcceptReceived(final JingleContentID contentID, JingleDescription description, JingleTransportPayload transportPayload) {
		logger_.fine("\n");
		if (!State.WaitingForAccept.equals(state)) { logger_.warning("Incorrect state\n"); return; }

		if (transportPayload instanceof JingleS5BTransportPayload) {
			JingleS5BTransportPayload s5bPayload = (JingleS5BTransportPayload)transportPayload;
			transporter.addRemoteCandidates(s5bPayload.getCandidates(), s5bPayload.getDstAddr());
			setState(State.TryingCandidates);
			transporter.startTryingRemoteCandidates();
		}
		else {
			logger_.fine("Unknown transport payload. Falling back.\n");
			fallback();
		}
	}

	public void handleSessionTerminateReceived(JinglePayload.Reason reason) {
		logger_.fine("\n");
		if (State.Finished.equals(state)) { logger_.warning("Incorrect state: " + state + "\n"); return; }

		stopAll();
		if (State.WaitForTermination.equals(state)) {
			waitForRemoteTermination.stop();
		}
		if (reason != null && JinglePayload.Reason.Type.Cancel.equals(reason.type)) {
			setFinishedState(FileTransfer.State.Type.Canceled, new FileTransferError(FileTransferError.Type.PeerError));
		}
		else if (reason != null && JinglePayload.Reason.Type.Decline.equals(reason.type)) {
			setFinishedState(FileTransfer.State.Type.Canceled, null);
		}
		else if (reason != null && JinglePayload.Reason.Type.Success.equals(reason.type)) {
			setFinishedState(FileTransfer.State.Type.Finished, null);
		}
		else {
			setFinishedState(FileTransfer.State.Type.Failed, new FileTransferError(FileTransferError.Type.PeerError));
		}
	}

	public void handleTransportAcceptReceived(final JingleContentID contentID, JingleTransportPayload transport) {
		logger_.fine("\n");
		if (!State.FallbackRequested.equals(state)) { logger_.warning("Incorrect state\n"); return; }

		if (transport instanceof JingleIBBTransportPayload) {
			JingleIBBTransportPayload ibbPayload = (JingleIBBTransportPayload)transport;
			startTransferring(transporter.createIBBSendSession(ibbPayload.getSessionID(), ( ibbPayload.getBlockSize() != null ? ibbPayload.getBlockSize() : DEFAULT_BLOCK_SIZE), stream));
		} 
		else {
			logger_.fine("Unknown transport replacement\n");
			terminate(JinglePayload.Reason.Type.FailedTransport);
		}
	}

	public void handleTransportRejectReceived(final JingleContentID contentID, JingleTransportPayload transport) {
		logger_.fine("\n");

		terminate(JinglePayload.Reason.Type.UnsupportedTransports);
	}

	protected void startTransferViaRemoteCandidate() {
		logger_.fine("\n");

		if (JingleS5BTransportPayload.Candidate.Type.ProxyType.equals(ourCandidateChoice.type)) {
			setState(State.WaitingForPeerProxyActivate);
		} 
		else {
			transportSession = createRemoteCandidateSession();
			startTransferringIfCandidateAcknowledged();
		}
	}

	protected void startTransferViaLocalCandidate() {
		logger_.fine("\n");

		if (JingleS5BTransportPayload.Candidate.Type.ProxyType.equals(theirCandidateChoice.type)) {
			setState(State.WaitingForLocalProxyActivate);
			transporter.startActivatingProxy(theirCandidateChoice.jid);
		} 
		else {
			transportSession = createLocalCandidateSession();
			startTransferringIfCandidateAcknowledged();
		}
	}

	private void startTransferringIfCandidateAcknowledged() {
		if (candidateAcknowledged) {
			startTransferring(transportSession);
		}
		else {
			setState(State.WaitingForCandidateAcknowledge);
		}
	}

	protected void handleLocalTransportCandidatesGenerated(final String s5bSessionID, final Vector<JingleS5BTransportPayload.Candidate> candidates, final String dstAddr) {
		logger_.fine("\n");
		if (!State.GeneratingInitialLocalCandidates.equals(state)) { logger_.warning("Incorrect state\n"); return; }

		fillCandidateMap(localCandidates, candidates);

		JingleFileTransferDescription description = new JingleFileTransferDescription();
		fileInfo.addHash(new HashElement("sha-1", new ByteArray()));
		fileInfo.addHash(new HashElement("md5", new ByteArray()));
		description.setFileInfo(fileInfo);

		JingleS5BTransportPayload transport = new JingleS5BTransportPayload();
		transport.setSessionID(s5bSessionID);
		transport.setMode(JingleS5BTransportPayload.Mode.TCPMode);
		transport.setDstAddr(dstAddr);
		for(JingleS5BTransportPayload.Candidate candidate : candidates) {
			transport.addCandidate(candidate);
			logger_.fine("\t" + "S5B candidate: " + candidate.hostPort.toString() + "\n");
		}
		setState(State.WaitingForAccept);
		session.sendInitiate(contentID, description, transport);
	}

	public void handleTransportInfoAcknowledged(final String id) {
		if (id.equals(candidateSelectRequestID)) {
			candidateAcknowledged = true;
		}
		if (State.WaitingForCandidateAcknowledge.equals(state)) {
			startTransferring(transportSession);
		}
	}

	protected JingleContentID getContentID() {
		return contentID;
	}

	protected void terminate(JinglePayload.Reason.Type reason) {
		logger_.fine(reason + "\n");

		if (!State.Initial.equals(state) && !State.GeneratingInitialLocalCandidates.equals(state) && !State.Finished.equals(state)) {
			session.sendTerminate(reason);
		}
		stopAll();
		setFinishedState(getExternalFinishedState(reason), getFileTransferError(reason));
	}

	protected void fallback() {
		if (options.isInBandAllowed()) {
			logger_.fine("Trying to fallback to IBB transport.\n");
			JingleIBBTransportPayload ibbTransport = new JingleIBBTransportPayload();
			ibbTransport.setBlockSize(DEFAULT_BLOCK_SIZE);
			ibbTransport.setSessionID(idGenerator.generateID());
			setState(State.FallbackRequested);
			session.sendTransportReplace(contentID, ibbTransport);
		}
		else {
			logger_.fine("Fallback to IBB transport not allowed.\n");
			terminate(JinglePayload.Reason.Type.ConnectivityError);
		}
	}

	private void handleTransferFinished(FileTransferError error) {
		logger_.fine("\n");
		if (!State.Transferring.equals(state)) { logger_.warning("Incorrect state: " + state + "\n"); return; }

		if (error != null) {
			terminate(JinglePayload.Reason.Type.ConnectivityError);
		} 
		else {
			sendSessionInfoHash();

			// wait for other party to terminate session after they have verified the hash
			setState(State.WaitForTermination);
			waitForRemoteTermination.start();
		}
	}

	private void sendSessionInfoHash() {
		logger_.fine("\n");

		JingleFileTransferHash hashElement = new JingleFileTransferHash();
		hashElement.getFileInfo().addHash(new HashElement("sha-1", hashCalculator.getSHA1Hash()));
		hashElement.getFileInfo().addHash(new HashElement("md5", hashCalculator.getMD5Hash()));
		session.sendInfo(hashElement);
	}

	protected void startTransferring(TransportSession transportSession) {
		logger_.fine("\n");

		this.transportSession = transportSession;
		processedBytesConnection = transportSession.onBytesSent.connect(onProcessedBytes);
		transferFinishedConnection = transportSession.onFinished.connect(new Slot1<FileTransferError>() {
			@Override
			public void call(FileTransferError e) {
				handleTransferFinished(e);
			}
		});
		setState(State.Transferring);
		transportSession.start();
	}

	protected boolean hasPriorityOnCandidateTie() {
		return true;
	}

	protected boolean isWaitingForPeerProxyActivate() {
		return State.WaitingForPeerProxyActivate.equals(state);
	}

	protected boolean isWaitingForLocalProxyActivate() {
		return State.WaitingForLocalProxyActivate.equals(state);
	}

	protected boolean isTryingCandidates() {
		return State.TryingCandidates.equals(state);
	}

	protected TransportSession createLocalCandidateSession() {
		return transporter.createLocalCandidateSession(stream, theirCandidateChoice);
	}

	protected TransportSession createRemoteCandidateSession() {
		return transporter.createRemoteCandidateSession(stream, ourCandidateChoice);
	}

	private void handleWaitForRemoteTerminationTimeout() {
		assert(state.equals(State.WaitForTermination));
		logger_.warning("Other party did not terminate session. Terminate it now.\n");
		waitForRemoteTermination.stop();
		terminate(JinglePayload.Reason.Type.MediaError);
	}

	private void stopAll() {
		logger_.fine(state + "\n");
		switch (state) {
			case Initial: logger_.warning("Not yet started\n"); break;
			case GeneratingInitialLocalCandidates: transporter.stopGeneratingLocalCandidates(); break;
			case WaitingForAccept: break;
			case TryingCandidates: transporter.stopTryingRemoteCandidates(); break;
			case FallbackRequested: break;
			case WaitingForPeerProxyActivate: break;
			case WaitingForLocalProxyActivate: transporter.stopActivatingProxy(); break;
			case WaitingForCandidateAcknowledge: // Fallthrough
			case Transferring:
				assert(transportSession != null);
				processedBytesConnection.disconnect();
				transferFinishedConnection.disconnect();
				transportSession.stop();
				transportSession = null;
				break;
			case WaitForTermination:
				break;
			case Finished: logger_.warning("Already finished\n"); break;
		}
		if (!State.Initial.equals(state)) {
			removeTransporter();
		}
	}

	private void setState(State state) {
		logger_.fine(state + "\n");
		this.state = state;
		onStateChanged.emit(new FileTransfer.State(getExternalState(state)));
	}

	private void setFinishedState(FileTransfer.State.Type type, final FileTransferError error) {
		logger_.fine("\n");
		this.state = State.Finished;
		onStateChanged.emit(new FileTransfer.State(type));
		onFinished.emit(error);
	}

	private static FileTransfer.State.Type getExternalState(State state) {
		switch (state) {
			case Initial: return FileTransfer.State.Type.Initial;
			case GeneratingInitialLocalCandidates: return FileTransfer.State.Type.WaitingForStart;
			case WaitingForAccept: return FileTransfer.State.Type.WaitingForAccept;
			case TryingCandidates: return FileTransfer.State.Type.Negotiating;
			case WaitingForPeerProxyActivate: return FileTransfer.State.Type.Negotiating;
			case WaitingForLocalProxyActivate: return FileTransfer.State.Type.Negotiating;
			case WaitingForCandidateAcknowledge: return FileTransfer.State.Type.Negotiating;
			case FallbackRequested: return FileTransfer.State.Type.Negotiating;
			case Transferring: return FileTransfer.State.Type.Transferring;
			case WaitForTermination: return FileTransfer.State.Type.Transferring;
			case Finished: return FileTransfer.State.Type.Finished;
		}
		assert(false);
		return FileTransfer.State.Type.Initial;
	}

}