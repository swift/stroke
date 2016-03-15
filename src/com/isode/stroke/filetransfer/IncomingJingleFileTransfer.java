/*
 * Copyright (c) 2010-2016 Isode Limited.
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
import java.util.Map;
import java.util.HashMap;

public class IncomingJingleFileTransfer extends JingleFileTransfer implements IncomingFileTransfer {

	private long fileSizeInBytes = 0; //FileTransferVariables
	private String filename = ""; //FileTransferVariables
	private String ft_description = ""; //FileTransferVariables

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
	public void setFileInfo(final String name, long size, String description) {
		this.filename = name;
		this.fileSizeInBytes = size;
		this.ft_description = description;
	}

	private JingleContentPayload initialContent;
	private CryptoProvider crypto;
	private State internalState;
	private JingleFileTransferDescription description;
	private WriteBytestream stream;
	private long receivedBytes;
	private IncrementalBytestreamHashCalculator hashCalculator;
	private Timer waitOnHashTimer;
	private Map<String, ByteArray> hashes = new HashMap<String, ByteArray>();
	private FileTransferOptions options;

	private SignalConnection writeStreamDataReceivedConnection;
	private SignalConnection waitOnHashTimerTickedConnection;
	private SignalConnection transferFinishedConnection;
	private Logger logger_ = Logger.getLogger(this.getClass().getName());

	public IncomingJingleFileTransfer(
		final JID toJID,
		JingleSession session,
		JingleContentPayload content,
		FileTransferTransporterFactory transporterFactory,
		TimerFactory timerFactory,
		CryptoProvider crypto) {
		super(session, toJID, transporterFactory);
		this.initialContent = content;
		this.crypto = crypto;
		this.internalState = State.Initial;
		this. receivedBytes = 0;
		this.hashCalculator = null;
		this.description = initialContent.getDescription(new JingleFileTransferDescription());
		assert(description != null);
		JingleFileTransferFileInfo fileInfo = description.getFileInfo();
		setFileInfo(fileInfo.getName(), fileInfo.getSize(), fileInfo.getDescription());
		hashes = fileInfo.getHashes();

		waitOnHashTimer = timerFactory.createTimer(5000);
		waitOnHashTimerTickedConnection = waitOnHashTimer.onTick.connect(new Slot() {
			@Override
			public void call() {
				handleWaitOnHashTimerTicked();
			}
		});
	}
	
	@Override
	protected void finalize() throws Throwable {
	    try {
	        destroy();
	    }
	    finally {
	        super.finalize();
	    }
	}
	
	/**
     * This replaces the C++ destructor.  After calling this object should not be used again.
     * If any methods are called after they behaviour is undefined and they may throw expections.
     */
	public void destroy() {
	    if (waitOnHashTimerTickedConnection != null) {
            waitOnHashTimerTickedConnection.disconnect();
            waitOnHashTimerTickedConnection = null;
        }
	    if (waitOnHashTimer != null) {
	        waitOnHashTimer.stop();
	        waitOnHashTimer = null;
	    }
	    hashCalculator = null;
	}

	/**
	* IncomingFileTransferMethod.
	*/
	@Override
	public void accept(WriteBytestream stream) {
		accept(stream, new FileTransferOptions());
	}

	/**
	* IncomingFileTransferMethod.
	*/
	@Override
	public void accept(WriteBytestream stream, final FileTransferOptions options) {
		logger_.fine("\n");
		if (!State.Initial.equals(internalState)) { logger_.warning("Incorrect state \n"); return; }

		assert(this.stream == null);
		this.stream = stream;
		this.options = options;

		assert(hashCalculator == null);

		hashCalculator = new IncrementalBytestreamHashCalculator(hashes.containsKey("md5"), hashes.containsKey("sha-1"), crypto);

		writeStreamDataReceivedConnection = stream.onWrite.connect(new Slot1<ByteArray>() {
			@Override
			public void call(ByteArray b) {
				handleWriteStreamDataReceived(b);
			}
		});

		if (initialContent.getTransport(new JingleS5BTransportPayload()) != null) {
			JingleS5BTransportPayload s5bTransport = initialContent.getTransport(new JingleS5BTransportPayload());
			logger_.fine("Got S5B transport as initial payload.\n");
			setTransporter(transporterFactory.createResponderTransporter(getInitiator(), getResponder(), s5bTransport.getSessionID(), options));
			transporter.addRemoteCandidates(s5bTransport.getCandidates(), s5bTransport.getDstAddr());
			setInternalState(State.GeneratingInitialLocalCandidates);
			transporter.startGeneratingLocalCandidates();
		}
		else if(initialContent.getTransport(new JingleIBBTransportPayload()) != null) {
			JingleIBBTransportPayload ibbTransport = initialContent.getTransport(new JingleIBBTransportPayload());
			logger_.fine("Got IBB transport as initial payload.\n");
			setTransporter(transporterFactory.createResponderTransporter(getInitiator(), getResponder(), ibbTransport.getSessionID(), options));

			startTransferring(transporter.createIBBReceiveSession(ibbTransport.getSessionID(), (int)description.getFileInfo().getSize(), stream));

			session.sendAccept(getContentID(), initialContent.getDescriptions().get(0), ibbTransport);
		}
		else {
			// Can't happen, because the transfer would have been rejected automatically
			assert(false);
		}
	}

	/**
	* IncomingFileTransferMethod.
	*/
	@Override
	public JID getSender() {
		return getInitiator();
	}

	/**
	* IncomingFileTransferMethod.
	*/
	@Override
	public JID getRecipient() {
		return getResponder();
	}

	/**
	* JingleFileTransferMethod.
	*/
	@Override
	public void cancel() {
		logger_.fine("\n");
		terminate(State.Initial.equals(internalState) ? JinglePayload.Reason.Type.Decline : JinglePayload.Reason.Type.Cancel);
	}

	protected void startTransferViaRemoteCandidate() {
		logger_.fine("\n");

		if (JingleS5BTransportPayload.Candidate.Type.ProxyType.equals(ourCandidateChoice.type)) {
			setInternalState(State.WaitingForPeerProxyActivate);
		} 
		else {
			startTransferring(createRemoteCandidateSession());
		}
	}

	protected void startTransferViaLocalCandidate() {
		logger_.fine("\n");

		if (JingleS5BTransportPayload.Candidate.Type.ProxyType.equals(theirCandidateChoice.type)) {
			setInternalState(State.WaitingForLocalProxyActivate);
			transporter.startActivatingProxy(theirCandidateChoice.jid);
		} 
		else {
			startTransferring(createLocalCandidateSession());
		}
	}
	
	protected void checkHashAndTerminate() {
		if (verifyData()) {
			terminate(JinglePayload.Reason.Type.Success);
		}
		else {
			logger_.warning("Hash verification failed\n");
			terminate(JinglePayload.Reason.Type.MediaError);
		}
	}

	protected void stopAll() {
		if (!State.Initial.equals(internalState)) {
			writeStreamDataReceivedConnection.disconnect();
			hashCalculator = null;
		}
		switch (internalState) {
			case Initial: break;
			case GeneratingInitialLocalCandidates: transporter.stopGeneratingLocalCandidates(); break;
			case TryingCandidates: transporter.stopTryingRemoteCandidates(); break;
			case WaitingForFallbackOrTerminate: break;
			case WaitingForPeerProxyActivate: break;
			case WaitingForLocalProxyActivate: transporter.stopActivatingProxy(); break;
			case WaitingForHash: // Fallthrough
			case Transferring:
				assert(transportSession != null);
				transferFinishedConnection.disconnect();
				transportSession.stop();
				transportSession = null;
				break;
			case Finished: logger_.warning("Already finished\n"); break;
		}
		if (!State.Initial.equals(internalState)) {
			removeTransporter();
		}
	}

	protected void setInternalState(State state) {
		logger_.fine(state + "\n");
		this.internalState = state;
		onStateChanged.emit(new FileTransfer.State(getExternalState(state)));
	}
	
	@Override
	public com.isode.stroke.filetransfer.FileTransfer.State getState() {
	    return new FileTransfer.State(getExternalState(internalState));
	}

	protected void setFinishedState(FileTransfer.State.Type type, final FileTransferError error) {
		logger_.fine("\n");
		this.internalState = State.Finished;
		onStateChanged.emit(new FileTransfer.State(type));
		onFinished.emit(error);
	}

	protected static FileTransfer.State.Type getExternalState(State state) {
		switch (state) {
			case Initial: return FileTransfer.State.Type.Initial;
			case GeneratingInitialLocalCandidates: return FileTransfer.State.Type.WaitingForStart;
			case TryingCandidates: return FileTransfer.State.Type.Negotiating;
			case WaitingForPeerProxyActivate: return FileTransfer.State.Type.Negotiating;
			case WaitingForLocalProxyActivate: return FileTransfer.State.Type.Negotiating;
			case WaitingForFallbackOrTerminate: return FileTransfer.State.Type.Negotiating;
			case Transferring: return FileTransfer.State.Type.Transferring;
			case WaitingForHash: return FileTransfer.State.Type.Transferring;
			case Finished: return FileTransfer.State.Type.Finished;
		}
		assert(false);
		return FileTransfer.State.Type.Initial;
	}

	protected boolean hasPriorityOnCandidateTie() {
		return false;
	}

	protected void fallback() {
		setInternalState(State.WaitingForFallbackOrTerminate);
	}

	protected void startTransferring(TransportSession transportSession) {
		logger_.fine("\n");

		this.transportSession = transportSession;
		transferFinishedConnection = transportSession.onFinished.connect(new Slot1<FileTransferError>() {
			@Override
			public void call(FileTransferError e) {
				handleTransferFinished(e);
			}
		});
		setInternalState(State.Transferring);
		transportSession.start();
	}

	protected boolean isWaitingForPeerProxyActivate() {
		return State.WaitingForPeerProxyActivate.equals(internalState);
	}

	protected boolean isWaitingForLocalProxyActivate() {
		return State.WaitingForLocalProxyActivate.equals(internalState);
	}

	protected boolean isTryingCandidates() {
		return State.TryingCandidates.equals(internalState);
	}

	protected TransportSession createLocalCandidateSession() {
		return transporter.createLocalCandidateSession(stream, theirCandidateChoice);
	}

	protected TransportSession createRemoteCandidateSession() {
		return transporter.createRemoteCandidateSession(stream, ourCandidateChoice);
	}

	protected void terminate(JinglePayload.Reason.Type reason) {
		logger_.fine(reason + "\n");

		if (!State.Finished.equals(internalState)) {
			session.sendTerminate(reason);
		}
		stopAll();
		setFinishedState(getExternalFinishedState(reason), getFileTransferError(reason));
	}

	private enum State {
		Initial,
		GeneratingInitialLocalCandidates,	
		TryingCandidates,
		WaitingForPeerProxyActivate,
		WaitingForLocalProxyActivate,
		WaitingForFallbackOrTerminate,
		Transferring,
		WaitingForHash,
		Finished
	};

	public void handleSessionTerminateReceived(JinglePayload.Reason reason) {
		logger_.fine("\n");
		if (State.Finished.equals(internalState)) { logger_.warning("Incorrect state\n"); return; }

		if (State.Finished.equals(internalState)) { 
			logger_.fine("Already terminated\n");
			return; 
		}

		stopAll();
		if (reason != null && JinglePayload.Reason.Type.Cancel.equals(reason.type)) {
			setFinishedState(FileTransfer.State.Type.Canceled, new FileTransferError(FileTransferError.Type.PeerError));
		}
		else if (reason != null && JinglePayload.Reason.Type.Success.equals(reason.type)) {
			setFinishedState(FileTransfer.State.Type.Finished, null);
		} 
		else {
			setFinishedState(FileTransfer.State.Type.Failed, new FileTransferError(FileTransferError.Type.PeerError));
		}
	}

	public void handleSessionInfoReceived(JinglePayload jinglePayload) {
		logger_.fine("\n");

		JingleFileTransferHash transferHash = jinglePayload.getPayload(new JingleFileTransferHash());
		if (transferHash != null) {
			logger_.fine("Received hash information.\n");
			waitOnHashTimer.stop();
			if (transferHash.getFileInfo().getHashes().containsKey("sha-1")) {
				hashes.put("sha-1", transferHash.getFileInfo().getHash("sha-1"));
			}
			if (transferHash.getFileInfo().getHashes().containsKey("md5")) {
				hashes.put("md5", transferHash.getFileInfo().getHash("md5"));
			}
			if (State.WaitingForHash.equals(internalState)) {
				checkHashAndTerminate();
			}
		}
		else {
			logger_.fine("Ignoring unknown session info\n");
		}
	}

	public void handleTransportReplaceReceived(final JingleContentID content, JingleTransportPayload transport) {
		logger_.fine("\n");
		if (!State.WaitingForFallbackOrTerminate.equals(internalState)) { 
			logger_.warning("Incorrect state\n"); 
			return; 
		}

		if (options.isInBandAllowed() && transport instanceof JingleIBBTransportPayload) {
			JingleIBBTransportPayload ibbTransport = (JingleIBBTransportPayload)transport;
			logger_.fine("transport replaced with IBB\n");

			startTransferring(transporter.createIBBReceiveSession(ibbTransport.getSessionID(), (int)description.getFileInfo().getSize(), stream));
			session.sendTransportAccept(content, ibbTransport);
		} 
		else {
			logger_.fine("Unknown replace transport\n");
			session.sendTransportReject(content, transport);
		}
	}

	protected void handleLocalTransportCandidatesGenerated(final String s5bSessionID, final Vector<JingleS5BTransportPayload.Candidate> candidates, final String dstAddr) {
		logger_.fine("\n");
		if (!State.GeneratingInitialLocalCandidates.equals(internalState)) { logger_.warning("Incorrect state\n"); return; }

		fillCandidateMap(localCandidates, candidates);

		JingleS5BTransportPayload transport = new JingleS5BTransportPayload();
		transport.setSessionID(s5bSessionID);
		transport.setMode(JingleS5BTransportPayload.Mode.TCPMode);
		transport.setDstAddr(dstAddr);
		for(JingleS5BTransportPayload.Candidate candidate : candidates) {
			transport.addCandidate(candidate);	
		}
		session.sendAccept(getContentID(), initialContent.getDescriptions().get(0), transport);

		setInternalState(State.TryingCandidates);
		transporter.startTryingRemoteCandidates();
	}

	private void handleWriteStreamDataReceived(final ByteArray data) {
		hashCalculator.feedData(data);
		receivedBytes += data.getSize();
		onProcessedBytes.emit(data.getSize());
		checkIfAllDataReceived();
	}

	private void stopActiveTransport() {

	}

	private void checkCandidateSelected() {

	}

	protected JingleContentID getContentID() {
		return new JingleContentID(initialContent.getName(), initialContent.getCreator());
	}

	private void checkIfAllDataReceived() {
		if (receivedBytes == getFileSizeInBytes()) {
			logger_.fine("All data received.\n");
			boolean hashInfoAvailable = false;
			for(final Map.Entry<String, ByteArray> hashElement : hashes.entrySet()) {
				hashInfoAvailable |= !(hashElement.getValue().isEmpty());
			}

			if (!hashInfoAvailable) {
				logger_.fine("No hash information yet. Waiting a while on hash info.\n");
				setInternalState(State.WaitingForHash);
				waitOnHashTimer.start();
			} 
			else {
				checkHashAndTerminate();
			}
		}
		else if (receivedBytes > getFileSizeInBytes()) {
			logger_.fine("We got more than we could handle!\n");
			terminate(JinglePayload.Reason.Type.MediaError);
		}
	}

	private boolean verifyData() {
		if (hashes.isEmpty()) {
			logger_.fine("no verification possible, skipping\n");
			return true;
		} 
		if (hashes.containsKey("sha-1")) {
			logger_.fine("Verify SHA-1 hash: " + (hashes.get("sha-1").equals(hashCalculator.getSHA1Hash())) + "\n");
			return hashes.get("sha-1").equals(hashCalculator.getSHA1Hash());
		}
		else if (hashes.containsKey("md5")) {
			logger_.fine("Verify MD5 hash: " + (hashes.get("md5").equals(hashCalculator.getMD5Hash())) + "\n");
			return hashes.get("md5").equals(hashCalculator.getMD5Hash());
		}
		else {
			logger_.fine("Unknown hash, skipping\n");
			return true;
		}
	}

	private void handleWaitOnHashTimerTicked() {
	    if (waitOnHashTimer == null) {
	        return;
	    }
		logger_.fine("\n");
		waitOnHashTimer.stop();
		terminate(JinglePayload.Reason.Type.Success);
	}

	private void handleTransferFinished(FileTransferError error) {
		if (error != null && !State.WaitingForHash.equals(internalState)) {
			terminate(JinglePayload.Reason.Type.MediaError);
		}
	}

    @Override
    public String getDescription() {
        return ft_description;
    }
}