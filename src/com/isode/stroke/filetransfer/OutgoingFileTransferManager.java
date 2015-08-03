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

import com.isode.stroke.signals.Signal1;
import com.isode.stroke.jingle.IncomingJingleSessionHandler;
import com.isode.stroke.jingle.JingleSessionManager;
import com.isode.stroke.jingle.JingleSession;
import com.isode.stroke.elements.JinglePayload;
import com.isode.stroke.elements.JingleContentPayload;
import com.isode.stroke.elements.JingleFileTransferDescription;
import com.isode.stroke.elements.JingleFileTransferFileInfo;
import com.isode.stroke.jingle.JingleSessionImpl;
import com.isode.stroke.elements.JingleS5BTransportPayload;
import com.isode.stroke.jingle.Jingle;
import com.isode.stroke.network.TimerFactory;
import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.jid.JID;
import com.isode.stroke.base.IDGenerator;

public class OutgoingFileTransferManager {

	private JingleSessionManager jingleSessionManager;
	private IQRouter iqRouter;
	private FileTransferTransporterFactory transporterFactory;
	private TimerFactory timerFactory;
	private IDGenerator idGenerator;
	private CryptoProvider crypto;

	public OutgoingFileTransferManager(
			JingleSessionManager jingleSessionManager, 
			IQRouter router, 
			FileTransferTransporterFactory transporterFactory,
			TimerFactory timerFactory,
			CryptoProvider crypto) {
		this.jingleSessionManager = jingleSessionManager;
		this.iqRouter = router;
		this.transporterFactory = transporterFactory;
		this.timerFactory = timerFactory;
		this.idGenerator = idGenerator;
		this.crypto = crypto;
		idGenerator = new IDGenerator();
	}

	public OutgoingFileTransfer createOutgoingFileTransfer(
			final JID from, 
			final JID recipient, 
			ReadBytestream readBytestream, 
			final JingleFileTransferFileInfo fileInfo,
			final FileTransferOptions config) {
		JingleSessionImpl jingleSession = new JingleSessionImpl(from, recipient, idGenerator.generateID(), iqRouter);
		jingleSessionManager.registerOutgoingSession(from, jingleSession);
		return new OutgoingJingleFileTransfer(
					recipient, 
					jingleSession, 
					readBytestream, 
					transporterFactory,
					timerFactory,
					idGenerator, 
					fileInfo, 
					config,
					crypto);
	}
}