/*
 * Copyright (c) 2010-2015 Isode Limited.
 * All rights reserved.
 * See the COPYING file for more information.
 */
/*
 * Copyright (c) 2015 Tarun Gupta.
 * Licensed under the simplified BSD license.
 * See Documentation/Licenses/BSD-simplified.txt for more information.
 */

package com.isode.stroke.filetransfer;

import com.isode.stroke.signals.Signal1;
import com.isode.stroke.jingle.IncomingJingleSessionHandler;
import com.isode.stroke.jingle.JingleSessionManager;
import com.isode.stroke.jingle.JingleSession;
import com.isode.stroke.elements.JinglePayload;
import com.isode.stroke.elements.JingleContentPayload;
import com.isode.stroke.elements.JingleFileTransferDescription;
import com.isode.stroke.elements.JingleS5BTransportPayload;
import com.isode.stroke.jingle.Jingle;
import com.isode.stroke.network.TimerFactory;
import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.jid.JID;
import java.util.logging.Logger;
import java.util.Vector;

public class IncomingFileTransferManager implements IncomingJingleSessionHandler {

	private JingleSessionManager jingleSessionManager;
	private FileTransferTransporterFactory transporterFactory;
	private TimerFactory timerFactory;
	private CryptoProvider crypto;
	private Logger logger_ = Logger.getLogger(this.getClass().getName());

	public IncomingFileTransferManager(
			JingleSessionManager jingleSessionManager,
			FileTransferTransporterFactory transporterFactory,
			TimerFactory timerFactory, 
			CryptoProvider crypto) {
		this.jingleSessionManager = jingleSessionManager;
		this.transporterFactory = transporterFactory;
		this.timerFactory = timerFactory;
		this.crypto = crypto;
		jingleSessionManager.addIncomingSessionHandler(this);
	}

	public Signal1<IncomingFileTransfer> onIncomingFileTransfer = new Signal1<IncomingFileTransfer>();

	public boolean handleIncomingJingleSession(
			JingleSession session, 
			final Vector<JingleContentPayload> contents, 
			final JID recipient) {
		if (Jingle.getContentWithDescription(contents, new JingleFileTransferDescription()) != null) {
			JingleContentPayload content = Jingle.getContentWithDescription(contents, new JingleFileTransferDescription());
			if (content.getTransport(new JingleS5BTransportPayload()) != null) {
				JingleFileTransferDescription description = content.getDescription(new JingleFileTransferDescription());
				if (description != null) {
					IncomingJingleFileTransfer transfer = new IncomingJingleFileTransfer(
							recipient, session, content, transporterFactory, timerFactory, crypto);
					onIncomingFileTransfer.emit(transfer);
				} 
				else {
					logger_.warning("Received a file-transfer request with no file description.");
					session.sendTerminate(JinglePayload.Reason.Type.FailedApplication);
				}
			}
			else {
				session.sendTerminate(JinglePayload.Reason.Type.UnsupportedTransports);
			}
			return true;
		}
		else {
			return false;
		}
	}
}