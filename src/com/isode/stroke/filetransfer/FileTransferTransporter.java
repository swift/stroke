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

/**
 * @brief The FileTransferTransporter class is an abstract factory definition
 * to generate SOCKS5 bytestream transports or IBB bytestreams for use in file
 * transfers.
 */
public abstract class FileTransferTransporter {

	public abstract void startGeneratingLocalCandidates();
	public abstract void stopGeneratingLocalCandidates();

	public abstract void addRemoteCandidates(
					final Vector<JingleS5BTransportPayload.Candidate> c, final String s);
	public abstract void startTryingRemoteCandidates();
	public abstract void stopTryingRemoteCandidates();

	public abstract void startActivatingProxy(final JID proxy);
	public abstract void stopActivatingProxy();

	public abstract TransportSession createIBBSendSession(
					final String sessionID, int blockSize, ReadBytestream r);
	public abstract TransportSession createIBBReceiveSession(
					final String sessionID, int size, WriteBytestream w);
	public abstract TransportSession createRemoteCandidateSession(
					ReadBytestream r, final JingleS5BTransportPayload.Candidate candidate);
	public abstract TransportSession createRemoteCandidateSession(
					WriteBytestream w, final JingleS5BTransportPayload.Candidate candidate);
	public abstract TransportSession createLocalCandidateSession(
					ReadBytestream r, final JingleS5BTransportPayload.Candidate candidate);
	public abstract TransportSession createLocalCandidateSession(
					WriteBytestream w, final JingleS5BTransportPayload.Candidate candidate);

	public final Signal3<String, Vector<JingleS5BTransportPayload.Candidate>, String> onLocalCandidatesGenerated = new Signal3<String, Vector<JingleS5BTransportPayload.Candidate>, String>();
	public final Signal2<String, JingleS5BTransportPayload.Candidate> onRemoteCandidateSelectFinished = new Signal2<String, JingleS5BTransportPayload.Candidate>();
	public final Signal2<String, ErrorPayload> onProxyActivated = new Signal2<String, ErrorPayload>();
}