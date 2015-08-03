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

import com.isode.stroke.network.ConnectionFactory;
import com.isode.stroke.network.ConnectionServerFactory;
import com.isode.stroke.crypto.CryptoProvider;
import com.isode.stroke.network.DomainNameResolver;
import com.isode.stroke.disco.EntityCapsProvider;
import com.isode.stroke.queries.IQRouter;
import com.isode.stroke.jid.JID;
import com.isode.stroke.jingle.JingleSessionManager;
import com.isode.stroke.network.NATTraverser;
import com.isode.stroke.network.NetworkEnvironment;
import com.isode.stroke.presence.PresenceOracle;
import com.isode.stroke.network.TimerFactory;
import com.isode.stroke.base.IDGenerator;
import com.isode.stroke.elements.DiscoInfo;
import com.isode.stroke.elements.Presence;
import com.isode.stroke.elements.JingleFileTransferFileInfo;
import java.io.File;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;
import java.util.Collection;

public class FileTransferManagerImpl extends FileTransferManager {

	private OutgoingFileTransferManager outgoingFTManager;
	private IncomingFileTransferManager incomingFTManager;
	private FileTransferTransporterFactory transporterFactory;
	private IQRouter iqRouter;
	private EntityCapsProvider capsProvider;
	private PresenceOracle presenceOracle;
	private IDGenerator idGenerator;
	private SOCKS5BytestreamRegistry bytestreamRegistry;
	private SOCKS5BytestreamProxiesManager bytestreamProxy;
	private SOCKS5BytestreamServerManager s5bServerManager;

	public FileTransferManagerImpl(
			final JID ownJID, 
			JingleSessionManager jingleSessionManager, 
			IQRouter router, 
			EntityCapsProvider capsProvider, 
			PresenceOracle presOracle, 
			ConnectionFactory connectionFactory,
			ConnectionServerFactory connectionServerFactory,
			TimerFactory timerFactory, 
			DomainNameResolver domainNameResolver,
			NetworkEnvironment networkEnvironment,
			NATTraverser natTraverser,
			CryptoProvider crypto) {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		this.iqRouter = router;
		this.capsProvider = capsProvider;
		this.presenceOracle = presOracle;
		bytestreamRegistry = new SOCKS5BytestreamRegistry();
		s5bServerManager = new SOCKS5BytestreamServerManager(bytestreamRegistry, connectionServerFactory, networkEnvironment, natTraverser);
		bytestreamProxy = new SOCKS5BytestreamProxiesManager(connectionFactory, timerFactory, domainNameResolver, iqRouter, new JID(ownJID.getDomain()));

		transporterFactory = new DefaultFileTransferTransporterFactory(
				bytestreamRegistry,
				s5bServerManager,
				bytestreamProxy,
				idGenerator,
				connectionFactory, 
				timerFactory, 
				crypto,
				iqRouter);
		outgoingFTManager = new OutgoingFileTransferManager(
				jingleSessionManager, 
				iqRouter, 
				transporterFactory,
				timerFactory,
				crypto);
		incomingFTManager = new IncomingFileTransferManager(
				jingleSessionManager, 
				iqRouter, 
				transporterFactory,
				timerFactory,
				crypto);
		incomingFTManager.onIncomingFileTransfer.connect(onIncomingFileTransfer);
	}

	public OutgoingFileTransfer createOutgoingFileTransfer(
			final JID to, 
			final String filepath, 
			final String description, 
			ReadBytestream bytestream) {
		return createOutgoingFileTransfer(to, filepath, description, bytestream, new FileTransferOptions());
	}

	public OutgoingFileTransfer createOutgoingFileTransfer(
			final JID to, 
			final String filepath, 
			final String description, 
			ReadBytestream bytestream,
			final FileTransferOptions config) {
		File file = new File(filepath);
		String filename = file.getName();
		long sizeInBytes = file.length();
		Date lastModified = new Date(file.lastModified());
		return createOutgoingFileTransfer(to, filename, description, sizeInBytes, lastModified, bytestream, config);
	}

	public OutgoingFileTransfer createOutgoingFileTransfer(
			final JID to,
			final String filename, 
			final String description, 
			final long sizeInBytes, 
			final Date lastModified, 
			ReadBytestream bytestream) {
		return createOutgoingFileTransfer(to, filename, description, sizeInBytes, lastModified, bytestream, new FileTransferOptions());
	}

	public OutgoingFileTransfer createOutgoingFileTransfer(
			final JID to,
			final String filename, 
			final String description, 
			final long sizeInBytes, 
			final Date lastModified, 
			ReadBytestream bytestream,
			final FileTransferOptions config) {
		JingleFileTransferFileInfo fileInfo = new JingleFileTransferFileInfo();
		fileInfo.setDate(lastModified);
		fileInfo.setSize(sizeInBytes);
		fileInfo.setName(filename);
		fileInfo.setDescription(description);
		
		JID receipient = to;
		
		if(receipient.isBare()) {
			JID fullJID = highestPriorityJIDSupportingFileTransfer(receipient);
			if (fullJID != null) {
				receipient = fullJID;
			} else {
				return null;
			}
		}

		assert(!iqRouter.getJID().isBare());

		return outgoingFTManager.createOutgoingFileTransfer(iqRouter.getJID(), receipient, bytestream, fileInfo, config);
	}

	public void start() {

	}

	public void stop() {
		s5bServerManager.stop();
	}

	private JID highestPriorityJIDSupportingFileTransfer(final JID bareJID) {
		JID fullReceipientJID = new JID();
		int priority = -2147483648;
		
		//getAllPresence(bareJID) gives you all presences for the bare JID (i.e. all resources) Isode Limited. @ 11:11
		Collection<Presence> presences = presenceOracle.getAllPresence(bareJID);

		//iterate over them
		for(Presence pres : presences) {
			if (pres.getPriority() > priority) {
				// look up caps from the jid
				DiscoInfo info = capsProvider.getCaps(pres.getFrom());
				if (isSupportedBy(info)) {
					priority = pres.getPriority();
					fullReceipientJID = pres.getFrom();
				}
			}
		}
		
		return fullReceipientJID.isValid() ? fullReceipientJID : null;
	}
}