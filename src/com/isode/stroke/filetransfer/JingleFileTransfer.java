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
import com.isode.stroke.elements.JingleS5BTransportPayload;
import com.isode.stroke.elements.ErrorPayload;
import com.isode.stroke.elements.JingleTransportPayload;
import com.isode.stroke.elements.JinglePayload;
import com.isode.stroke.jingle.JingleContentID;
import com.isode.stroke.jingle.AbstractJingleSessionListener;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot3;
import com.isode.stroke.signals.Slot2;
import java.util.Map;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Logger;

public abstract class JingleFileTransfer extends AbstractJingleSessionListener {

	protected JingleSession session;
	protected JID target;
	protected FileTransferTransporterFactory transporterFactory;
	protected FileTransferTransporter transporter;

	protected String candidateSelectRequestID;
	protected boolean ourCandidateSelectFinished;
	protected JingleS5BTransportPayload.Candidate ourCandidateChoice;
	protected boolean theirCandidateSelectFinished;
	protected JingleS5BTransportPayload.Candidate theirCandidateChoice;
	protected Map<String, JingleS5BTransportPayload.Candidate> localCandidates = new HashMap<String, JingleS5BTransportPayload.Candidate>();

	protected TransportSession transportSession;

	protected SignalConnection localTransportCandidatesGeneratedConnection;
	protected SignalConnection remoteTransportCandidateSelectFinishedConnection;
	protected SignalConnection proxyActivatedConnection;
	private Logger logger_ = Logger.getLogger(this.getClass().getName());

	public JingleFileTransfer(
			JingleSession session, 
			final JID target,
			FileTransferTransporterFactory transporterFactory) {
		this.session = session;
		this.target = target;
		this.transporterFactory = transporterFactory;
		this.transporter = null;
		this.ourCandidateSelectFinished = false;
		this.theirCandidateSelectFinished = false;
		session.addListener(this);
	}

	public void handleTransportInfoReceived(final JingleContentID contentID, JingleTransportPayload transport) {
		logger_.fine("\n");
		if(transport instanceof JingleS5BTransportPayload) {
			JingleS5BTransportPayload s5bPayload = (JingleS5BTransportPayload)transport;
			if (s5bPayload.hasCandidateError() || !s5bPayload.getCandidateUsed().isEmpty()) {
				logger_.fine("Received candidate decision from peer\n");
				if (!isTryingCandidates()) {logger_.warning("Incorrect state\n"); return; }

				theirCandidateSelectFinished = true;
				if (!s5bPayload.hasCandidateError()) {
					if(!(localCandidates.containsKey(s5bPayload.getCandidateUsed()))) {
						logger_.warning("Got invalid candidate\n");
						terminate(JinglePayload.Reason.Type.GeneralError);
						return;
					}
					theirCandidateChoice = localCandidates.get(s5bPayload.getCandidateUsed());
				}
				decideOnCandidates();
			} 
			else if (!s5bPayload.getActivated().isEmpty()) {
				logger_.fine("Received peer activate from peer\n");
				if (!isWaitingForPeerProxyActivate()) { logger_.warning("Incorrect state\n"); return; }

				if (ourCandidateChoice.cid.equals(s5bPayload.getActivated())) {
					startTransferring(createRemoteCandidateSession());
				} 
				else {
					logger_.warning("ourCandidateChoice doesn't match activated proxy candidate!\n");
					terminate(JinglePayload.Reason.Type.GeneralError);
				}
			}
			else if (s5bPayload.hasProxyError()) {
				logger_.fine("Received proxy error. Trying to fall back to IBB.\n");
				fallback();
			}
			else {
				logger_.fine("Ignoring unknown info\n");
			}
		}
		else {
			logger_.fine("Ignoring unknown info\n");
		}
	}

	protected abstract void handleLocalTransportCandidatesGenerated(
			final String s5bSessionID, 
			final Vector<JingleS5BTransportPayload.Candidate> candidates,
			final String dstAddr);

	protected void handleProxyActivateFinished(final String s5bSessionID, ErrorPayload error) {
		logger_.fine("\n");
		if (!isWaitingForLocalProxyActivate()) { logger_.warning("Incorrect state\n"); return; }

		if (error != null) {
			logger_.fine("Error activating proxy\n");
			JingleS5BTransportPayload proxyError = new JingleS5BTransportPayload();
			proxyError.setSessionID(s5bSessionID);
			proxyError.setProxyError(true);
			session.sendTransportInfo(getContentID(), proxyError);
			fallback();
		} 
		else {
			JingleS5BTransportPayload proxyActivate = new JingleS5BTransportPayload();
			proxyActivate.setSessionID(s5bSessionID);
			proxyActivate.setActivated(theirCandidateChoice.cid);
			session.sendTransportInfo(getContentID(), proxyActivate);
			startTransferring(createLocalCandidateSession());
		}
	}

	protected void decideOnCandidates() {
		logger_.fine("\n");
		if (!ourCandidateSelectFinished || !theirCandidateSelectFinished) {
			logger_.fine("Can't make a decision yet!\n");
			return;
		}
		if (ourCandidateChoice == null && theirCandidateChoice == null) {
			logger_.fine("No candidates succeeded.\n");
			fallback();
		}
		else if (ourCandidateChoice != null && theirCandidateChoice == null) {
			logger_.fine("Start transfer using remote candidate: " + ourCandidateChoice.cid + ".\n");
			startTransferViaRemoteCandidate();
		}
		else if (theirCandidateChoice != null && ourCandidateChoice == null) {
			logger_.fine("Start transfer using local candidate: " + theirCandidateChoice.cid + ".\n");
			startTransferViaLocalCandidate();
		}
		else {
			logger_.fine("Choosing between candidates " 
				+ ourCandidateChoice.cid + "(" + ourCandidateChoice.priority + ")" + " and " 
				+ theirCandidateChoice.cid + "(" + theirCandidateChoice.priority + ")\n");
			if (ourCandidateChoice.priority > theirCandidateChoice.priority) {
				logger_.fine("Start transfer using remote candidate: " + ourCandidateChoice.cid + ".\n");
				startTransferViaRemoteCandidate();
			}
			else if (ourCandidateChoice.priority < theirCandidateChoice.priority) {
				logger_.fine("Start transfer using local candidate:" + theirCandidateChoice.cid + ".\n");
				startTransferViaLocalCandidate();
			}
			else {
				if (hasPriorityOnCandidateTie()) {
					logger_.fine("Start transfer using remote candidate: " + ourCandidateChoice.cid + "\n");
					startTransferViaRemoteCandidate();
				}
				else {
					logger_.fine("Start transfer using local candidate: " + theirCandidateChoice.cid + "\n");
					startTransferViaLocalCandidate();
				}
			}
		}
	}

	protected void handleRemoteTransportCandidateSelectFinished(final String s5bSessionID, final JingleS5BTransportPayload.Candidate candidate) {
		logger_.fine("\n");

		ourCandidateChoice = candidate;
		ourCandidateSelectFinished = true;

		JingleS5BTransportPayload s5bPayload = new JingleS5BTransportPayload();
		s5bPayload.setSessionID(s5bSessionID);
		if (candidate != null) {
			s5bPayload.setCandidateUsed(candidate.cid);
		}
		else {
			s5bPayload.setCandidateError(true);
		}
		candidateSelectRequestID = session.sendTransportInfo(getContentID(), s5bPayload);

		decideOnCandidates();
	}

	protected abstract JingleContentID getContentID();
	protected abstract void startTransferring(TransportSession session);
	protected abstract void terminate(JinglePayload.Reason.Type reason);
	protected abstract void fallback();
	protected abstract boolean hasPriorityOnCandidateTie();
	protected abstract boolean isWaitingForPeerProxyActivate();
	protected abstract boolean isWaitingForLocalProxyActivate();
	protected abstract boolean isTryingCandidates();
	protected abstract TransportSession createLocalCandidateSession();
	protected abstract TransportSession createRemoteCandidateSession();
	protected abstract void startTransferViaLocalCandidate();
	protected abstract void startTransferViaRemoteCandidate();


	protected void setTransporter(FileTransferTransporter transporter) {
		//SWIFT_LOG_ASSERT(!this.transporter, error);
		this.transporter = transporter;
		localTransportCandidatesGeneratedConnection = transporter.onLocalCandidatesGenerated.connect(new Slot3<String, Vector<JingleS5BTransportPayload.Candidate>, String>() {
			@Override
			public void call(String s, Vector<JingleS5BTransportPayload.Candidate> v, String t) {
				handleLocalTransportCandidatesGenerated(s, v, t);
			}
		});
		remoteTransportCandidateSelectFinishedConnection = transporter.onRemoteCandidateSelectFinished.connect(new Slot2<String, JingleS5BTransportPayload.Candidate>() {
			@Override
			public void call(String s, JingleS5BTransportPayload.Candidate c) {
				handleRemoteTransportCandidateSelectFinished(s, c);
			}
		});
		proxyActivatedConnection = transporter.onProxyActivated.connect(new Slot2<String, ErrorPayload>() {
			@Override
			public void call(String s, ErrorPayload e) {
				handleProxyActivateFinished(s, e);
			}
		});
	}

	protected void removeTransporter() {
		if (transporter != null) {
			localTransportCandidatesGeneratedConnection.disconnect();
			remoteTransportCandidateSelectFinishedConnection.disconnect();
			proxyActivatedConnection.disconnect();
			transporter = null;
		}
	}

	protected void fillCandidateMap(Map<String, JingleS5BTransportPayload.Candidate> map, final Vector<JingleS5BTransportPayload.Candidate> candidates) {
		map.clear();
		for (JingleS5BTransportPayload.Candidate candidate : candidates) {
			map.put(candidate.cid, candidate);
		}
	}

	/*
	std.string JingleFileTransfer.getS5BDstAddr(const JID& requester, const JID& target) const {
		return Hexify.hexify(crypto.getSHA1Hash(
					createSafeByteArray(s5bSessionID + requester.toString() + target.toString())));
	}
	*/

	protected JID getInitiator() {
		return session.getInitiator();
	}

	protected JID getResponder() {
		return target;
	}

	protected static FileTransfer.State.Type getExternalFinishedState(JinglePayload.Reason.Type reason) {
		if (reason.equals(JinglePayload.Reason.Type.Cancel) || reason.equals(JinglePayload.Reason.Type.Decline)) {
			return FileTransfer.State.Type.Canceled;
		}
		else if (reason.equals(JinglePayload.Reason.Type.Success)) {
			return FileTransfer.State.Type.Finished;
		}
		else {
			return FileTransfer.State.Type.Failed;
		}
	}

	protected static FileTransferError getFileTransferError(JinglePayload.Reason.Type reason) {
		if (reason.equals(JinglePayload.Reason.Type.Success)) {
			return null;
		}
		else {
			return new FileTransferError(FileTransferError.Type.UnknownError);
		}
	}
}