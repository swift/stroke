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

import java.util.PriorityQueue;
import com.isode.stroke.network.ConnectionFactory;
import com.isode.stroke.network.TimerFactory;
import com.isode.stroke.network.Connection;
import com.isode.stroke.signals.SignalConnection;
import com.isode.stroke.signals.Slot1;
import com.isode.stroke.signals.Signal2;
import com.isode.stroke.elements.JingleS5BTransportPayload;
import java.util.logging.Logger;
import java.util.Vector;

public class RemoteJingleTransportCandidateSelector {

	private ConnectionFactory connectionFactory;
	private TimerFactory timerFactory;

	private PriorityQueue<JingleS5BTransportPayload.Candidate> candidates = new PriorityQueue<JingleS5BTransportPayload.Candidate>(25, new JingleS5BTransportPayload.CompareCandidate());
	private SOCKS5BytestreamClientSession s5bSession;
	private SignalConnection sessionReadyConnection;
	private JingleS5BTransportPayload.Candidate lastCandidate;
	private String socks5DstAddr;
	private FileTransferOptions options;
	private Logger logger_ = Logger.getLogger(this.getClass().getName());

	public RemoteJingleTransportCandidateSelector(ConnectionFactory connectionFactory, TimerFactory timerFactory, final FileTransferOptions options) {
		this.connectionFactory = connectionFactory;
		this.timerFactory = timerFactory;
		this.options = options;
	}

	public void addCandidates(final Vector<JingleS5BTransportPayload.Candidate> candidates) {
		for(JingleS5BTransportPayload.Candidate c : candidates) {
			this.candidates.add(c);
		}
	}

	public void setSOCKS5DstAddr(final String socks5DstAddr) {
		this.socks5DstAddr = socks5DstAddr;
	}

	public void startSelectingCandidate() {
		tryNextCandidate();
	}

	public void stopSelectingCandidate() {
		if (s5bSession != null) {
			sessionReadyConnection.disconnect();
			s5bSession.stop();
		}
	}

	public Signal2<JingleS5BTransportPayload.Candidate, SOCKS5BytestreamClientSession> onCandidateSelectFinished = new Signal2<JingleS5BTransportPayload.Candidate, SOCKS5BytestreamClientSession>();

	private void tryNextCandidate() {
		if (candidates.isEmpty()) {
			logger_.fine("No more candidates\n");
			onCandidateSelectFinished.emit(null, null);
		} 
		else {
			lastCandidate = candidates.peek();
			candidates.poll();
			logger_.fine("Trying candidate " + lastCandidate.cid + "\n");
			if ((lastCandidate.type.equals(JingleS5BTransportPayload.Candidate.Type.DirectType) && options.isDirectAllowed()) ||
				(lastCandidate.type.equals(JingleS5BTransportPayload.Candidate.Type.AssistedType) && options.isAssistedAllowed()) ||
				(lastCandidate.type.equals(JingleS5BTransportPayload.Candidate.Type.ProxyType) && options.isProxiedAllowed())) {
				Connection connection = connectionFactory.createConnection();
				s5bSession = new SOCKS5BytestreamClientSession(connection, lastCandidate.hostPort, socks5DstAddr, timerFactory);
				sessionReadyConnection = s5bSession.onSessionReady.connect(new Slot1<Boolean>() {
					@Override
					public void call(Boolean b) {
						handleSessionReady(b);
					}
				});
				s5bSession.start();
			} 
			else {
				logger_.fine("Can't handle this type of candidate\n");
				tryNextCandidate();
			}
		}
	}

	private void handleSessionReady(boolean error) {
		sessionReadyConnection.disconnect();
		if (error) {
			s5bSession = null;
			tryNextCandidate();
		}
		else {
			onCandidateSelectFinished.emit(lastCandidate, s5bSession);
		}
	}
}